package io.mlqs.es;
import io.mlqs.es.entity.DocumentSearchResultEntity;
import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import io.mlqs.es.legal.services.LegalService;
import io.mlqs.es.legal.services.MarriageLawServiceImpl;
import io.mlqs.utils.EmbeddingUtils;
import io.mlqs.utils.RerankUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService{
    @Autowired
    private EmbeddingUtils embeddingUtils;

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
    @PostConstruct
    public void init(){
        legalServices = Map.of(
                Legal.CivilCode, civilCodeService,
                Legal.MarriageRegistration, marriageRegistrationService,
                Legal.MarriageLaw, new MarriageLawServiceImpl()
        );
    }

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
        DocumentSearchResultEntity result = DocumentSearchResultEntity.build();
        List<Float> vector = EmbeddingUtils.typeToFloat(embeddingUtils.toVector(context));
        for (String name: legalNames.keySet()){
            //获取法律文件对应的服务类
            LegalService service = getLegalService(name);
            if (service == null)
                continue;

            //获取法律实体类
            LegalEntity legalEntity = service.getLegalEntity();
            List<LegalRecordEntity> qr = service.query(context, vector, legalNames.get(name));
            //反转qr
            qr = (List<LegalRecordEntity>) RerankUtils.reverse(qr);
            legalEntity.setRecords(qr);
            //放入结果
            result.put(name, legalEntity);
        }
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
