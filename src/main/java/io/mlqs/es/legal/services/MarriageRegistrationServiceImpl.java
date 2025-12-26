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

        Future<List<MarriageRegistrationRecordEntity>> knn = executor.submit(() -> {
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
            return ms;
        });

        Future<List<MarriageRegistrationRecordEntity>> keyword = executor.submit(() -> {
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
            return ms2;
        });

        Set<MarriageRegistrationRecordEntity> set = new LinkedHashSet<>();
        try {
            set.addAll(knn.get());
            set.addAll(keyword.get());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.log(MarriageRegistrationServiceImpl.class, "KNN检索或关键字检索出错");
            return new ArrayList<>();
        }
        List<MarriageRegistrationRecordEntity> ms3 = set.stream().toList();
        List<String> documents = new ArrayList<>();
        for (MarriageRegistrationRecordEntity ms1 : ms3) {
            documents.add(ms1.getChapter() + " " + ms1.getChapterName() + " " + ms1.getItem() + " " + ms1.getContent());
        }
        List<MultiGroup> rerank = rerankUtils.rerank(context, documents, 5, threshold);
        List<LegalRecordEntity> final_ms = new ArrayList<>();
        for (MultiGroup mg : rerank) {
            MarriageRegistrationRecordEntity marriageRegistrationRecordEntity = ms3.get(mg.get(0));
            marriageRegistrationRecordEntity.setVector( null);
            final_ms.add(marriageRegistrationRecordEntity);
        }
        executor.shutdown();
        return final_ms;
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new MarriageRegistrationEntity();
    }
}
