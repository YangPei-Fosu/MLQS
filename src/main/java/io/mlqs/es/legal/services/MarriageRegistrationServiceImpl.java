package io.mlqs.es.legal.services;

import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.db.MarriageLawRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import io.mlqs.es.legal.entity.MarriageRegistrationEntity;
import io.mlqs.utils.LogUtils;
import io.mlqs.utils.RerankUtils;
import io.mlqs.utils.clazz.MultiGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class MarriageRegistrationServiceImpl implements LegalService{
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private RerankUtils rerankUtils;

    /**
     * 婚姻登记条例
     * 基本逻辑:
     * 1. 先用KNN查询，得到相似的条款20条，进行重排列取10条
     * 2. 再用关键字查询，得到相似的条款15条，进行重排列取5条
     * 3. 合并rerank结果并去重，进行重排列取5条最终结果
     */
    @Override
    public List<LegalRecordEntity> query(String context, List<Float> vector, double threshold) {
        //创建两个子线程，分别完成关键字查询和knn查询
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<MultiGroup> knn_rerankF = executor.submit(() -> {
            //knn查询
            int k = 15;
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .knn(kb -> kb
                                    .field("vector")
                                    .queryVector(vector)
                                    .numCandidates(k * 2)))
                    .withPageable(PageRequest.of(0, k))
                    .build();
            SearchHits<MarriageRegistrationRecordEntity> searchHits = elasticsearchTemplate.search(query, MarriageRegistrationRecordEntity.class);
            List<MarriageRegistrationRecordEntity> ms = searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
            //knn重排
            List<String> documents = new ArrayList<>();
            for (MarriageRegistrationRecordEntity m : ms)
                documents.add(m.getChapter() + m.getChapterName() + m.getItem() + m.getContent());
            List<MultiGroup> knn_rerank = rerankUtils.rerank(context, documents, 5, threshold);
            MultiGroup mg = MultiGroup.of(ms, knn_rerank);
            return mg;
        });

        Future<MultiGroup> keyword_rerankF = executor.submit(() -> {
            //关键字查询
            NativeQuery query2 = NativeQuery.builder()
                    .withQuery(q -> q
                            .queryString(qs -> qs
                                    .fields("content", "chapterName", "item", "chapter")
                                    .query(context)))
                    .build();
            SearchHits<MarriageRegistrationRecordEntity> searchHits2 = elasticsearchTemplate.search(query2, MarriageRegistrationRecordEntity.class);
            List<MarriageRegistrationRecordEntity> ms2 = searchHits2.getSearchHits().stream().map(SearchHit::getContent).toList();
            if (ms2.size() > 10)
                ms2 = ms2.subList(0, 10);
            //关键字重排
            List<String> documents2 = new ArrayList<>();
            for (MarriageRegistrationRecordEntity m : ms2)
                documents2.add(m.getChapter() + m.getChapterName() + m.getItem() + m.getContent());
            List<MultiGroup> keyword_rerank = rerankUtils.rerank(context, documents2, 5, threshold);
            MultiGroup mg = MultiGroup.of(ms2, keyword_rerank);
            return mg;
        });

        //获取结果
        List<MarriageRegistrationRecordEntity> ms = null;
        List<MarriageRegistrationRecordEntity> ms2 = null;
        List<MultiGroup> knn_rerankL = null;
        List<MultiGroup> keyword_rerankL = null;
        try {
            MultiGroup mg = knn_rerankF.get();
            ms = mg.get(0);
            knn_rerankL = mg.get(1);
            MultiGroup mg2 = keyword_rerankF.get();
            ms2 = mg2.get(0);
            keyword_rerankL = mg2.get(1);
        }catch (Exception e) {
            e.printStackTrace();
            LogUtils.log(MarriageLawServiceImpl.class, "KNN检索或关键字检索出错");
        }
        //合并rerank结果并去重，使用LinkedHashSet确保顺序
        Set<MarriageRegistrationRecordEntity> ms3 = new LinkedHashSet<>();
        Set<String> documents3 = new LinkedHashSet<>();
        for (MultiGroup mg : knn_rerankL) {
            ms3.add(ms.get(mg.get(0)));
            documents3.add(mg.get(2));
        }
        for (MultiGroup mg : keyword_rerankL) {
            ms3.add(ms2.get(mg.get(0)));
            documents3.add(mg.get(2));
        }

        //最后对得到的documents3再次rerank
        List<MultiGroup> final_rerank = rerankUtils.rerank(context, documents3.stream().toList(), 5, threshold);
        List<LegalRecordEntity> final_ms = new ArrayList<>();
        List<MarriageRegistrationRecordEntity> ms4 = ms3.stream().toList();
        for (MultiGroup mg : final_rerank) {
            MarriageRegistrationRecordEntity me = ms4.get(mg.get(0));
            me.setVector(null);
            final_ms.add(me);
        }

        executor.shutdown();

        return final_ms;
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new MarriageRegistrationEntity();
    }
}
