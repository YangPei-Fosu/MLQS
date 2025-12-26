package io.mlqs.es;
import io.mlqs.es.cases.CaseService;
import io.mlqs.es.cases.db.CaseEntity;
import io.mlqs.es.entity.DocumentSearchResultEntity;
import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import io.mlqs.es.legal.services.LegalService;
import io.mlqs.utils.EmbeddingUtils;
import io.mlqs.utils.LogUtils;
import io.mlqs.utils.RerankUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class DocumentServiceImpl implements DocumentService{
    @Autowired
    private EmbeddingUtils embeddingUtils;

    /**
     * 执行搜索的线程池大小
     */
    @Value("${mlqs.search.thread-pool-size}")
    private int threadPoolSize;
    /**
     * 搜索线程池
     */
    private ExecutorService searchPool;
    /**
     * 是否使用并行搜索
     */
    @Value("${mlqs.search.use-parallel-search}")
    private boolean parallel;

    /**
     * 法律文献的Map【法律名，对应的服务类】
     */
    private Map<Legal, LegalService> legalServices;

    /**
     * 需要新增法律的接口时只需要添加一个枚举项，并添加一个对应的服务类
     */
    private enum Legal{
        CivilCode,
        MarriageRegistration,
        MarriageLaw
    }

    /**
     * 注入对应的服务类
     */
    @Autowired
    @Qualifier("civilCodeServiceImpl")
    private LegalService civilCodeService;
    @Autowired
    @Qualifier("marriageRegistrationServiceImpl")
    private LegalService marriageRegistrationService;
    @Autowired
    @Qualifier("marriageLawServiceImpl")
    private LegalService marriageLawServiceImpl;
    @PostConstruct
    public void init(){
        legalServices = Map.of(
                Legal.CivilCode, civilCodeService,
                Legal.MarriageRegistration, marriageRegistrationService,
                Legal.MarriageLaw, marriageLawServiceImpl
        );
        //如果不使用并行搜索，就不需要创建线程池
        if(parallel == true){
            //创建线程池
            searchPool = Executors.newFixedThreadPool(threadPoolSize);
            LogUtils.log(DocumentServiceImpl.class, "已启用并行检索");
            LogUtils.log(DocumentServiceImpl.class, "创建检索线程池成功，线程数：" + threadPoolSize);
        }else {
            LogUtils.log(DocumentServiceImpl.class, "已禁用并行检索");
        }
    }

    /**
     * 获取案件库中高于阈值的法律案例
     */
    @Autowired
    private CaseService caseService;


    /**
     * 获取法律对应的服务类
     * @param name
     * @return 服务类
     */
    public LegalService getLegalService(String name){
        for (Legal l: Legal.values()) {
            if(l.name().equals(name))
                return legalServices.get(l);
        }
        return null;
    }

    /**
     * 检索各个法律文献
     * @param context 检索内容
     * @param legalNames 需要检索的法律文件名字，注意是在枚举中的:[CivilCode,MarriageRegistration,MarriageLaw]，值为关联度阈值
     * @return 搜索结果
     */
    @Override
    public DocumentSearchResultEntity hybridSearch(String context, Map<String, Double> legalNames) {
        //开始检索时间点
        long start = System.currentTimeMillis();
        DocumentSearchResultEntity result = DocumentSearchResultEntity.build();
        List<Float> vector = EmbeddingUtils.typeToFloat(embeddingUtils.toVector(context));
        LogUtils.log(DocumentServiceImpl.class, "开始检索:"+ legalNames.keySet());
        //如果没有使用并行搜索，或者只有一个检索，则直接顺序检索全部的文件
        if(parallel == false || legalNames.size() == 1){
            LogUtils.log(DocumentServiceImpl.class, "开始顺序检索");
            for (String name: legalNames.keySet()) {
                if(name != "CASE_ENTITY"){
                    LegalService service = getLegalService(name);
                    List<LegalRecordEntity> query = service.query(context, vector, legalNames.get(name));
                    List<LegalRecordEntity> reverse = (List<LegalRecordEntity>) RerankUtils.reverse(query);
                    LegalEntity legalEntity = service.getLegalEntity();
                    legalEntity.setRecords(reverse);
                    result.put(name, legalEntity);
                }
                else{
                    List<CaseEntity> query = caseService.query(context,vector,legalNames.get("CASE_ENTITY"));
                    result.put(query);
                }
            }
        }
        else {
            LogUtils.log(DocumentServiceImpl.class, "开始并行检索");
            //处理案件检索请求
            Future<List<CaseEntity>> caseFuture = null;
            if(legalNames.containsKey("CASE_ENTITY")) {
                //移除案件检索请求避免对后续检索请求造成干扰
                legalNames.remove("CASE_ENTITY");
                Double threshold = legalNames.get("CASE_ENTITY");
                if (threshold == null || threshold == 0)
                    threshold = 0.5;
                Double finalThreshold = threshold;
                caseFuture = searchPool.submit(() -> caseService.query(context, vector, finalThreshold));
            }

            //收集线程执行结果集
            Map<String, Future<List<LegalRecordEntity>>> futuresMap = new HashMap<>();
            //获取需要检索的法律文件的名称
            for (String name: legalNames.keySet()) {
                LegalService service = getLegalService(name);
                //线程池执行并行的异步搜索
                Future<List<LegalRecordEntity>> listFuture = searchPool.submit(() ->{
                            //查询库表
                            List<LegalRecordEntity> query = service.query(context, vector, legalNames.get(name));
                            //反转结果集
                            List<LegalRecordEntity> reverse = (List<LegalRecordEntity>) RerankUtils.reverse(query);
                            return reverse;
                        }
                );
                futuresMap.put(name, listFuture);
            }

            //处理案件
            try{
                if(caseFuture != null){
                    List<CaseEntity> query = caseFuture.get();
                    //案件放入结果集
                    result.put(query);
                }
            }catch (Exception e){
                LogUtils.log(DocumentServiceImpl.class, "案件查询失败" + e.getMessage());
                e.printStackTrace();
            }

            //等待所有任务完成
            for (String name: futuresMap.keySet()){
                Future<List<LegalRecordEntity>> future = futuresMap.get(name);
                try {
                    //获取异步结果
                    List<LegalRecordEntity> qr = future.get();
                    //获取法律实体类
                    LegalService service = getLegalService(name);
                    LegalEntity legalEntity = service.getLegalEntity();
                    legalEntity.setRecords(qr);
                    // 放入结果集
                    result.put(name, legalEntity);
                } catch (Exception e) {
                    // 处理异常
                    LogUtils.log(DocumentServiceImpl.class, "法律文件查询失败" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        //结束检索时间点
        long end = System.currentTimeMillis();
        //耗时秒
        long time = (end - start) / 1000;
        //毫秒
        long millis = (end - start) - (time * 1000);
        LogUtils.log(DocumentServiceImpl.class, "检索耗时：" + time + "秒" + millis + "毫秒");
        return result;
    }

    @Override
    public Map<String, String> getUsableLegalName() {
        Map<String, String> map = new HashMap<>();
        for (Legal l: Legal.values()) {
            map.put(l.name(), getLegalService(l.name()).getLegalEntity().getLawName());
        }
        return map;
    }
}
