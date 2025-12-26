package io.mlqs.es.legal.services;

import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.db.MarriageLawRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import io.mlqs.es.legal.entity.MarriageLawEntity;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class MarriageLawServiceImpl implements LegalService{
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private RerankUtils rerankUtils;

    @Override
    public List<LegalRecordEntity> query(String context, List<Float> vector, double threshold) {
        //创建两个子线程，分别完成关键字查询和knn查询
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //knn查询
        Future<List<MarriageLawRecordEntity>> knn = executor.submit(() -> {
            //设置k
            int k = 15;
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .knn(kb -> kb
                                    .field("vector")
                                    .queryVector(vector)
                                    .numCandidates(k * 2)))
                    .withPageable(PageRequest.of(0, k))
                    .build();
            SearchHits<MarriageLawRecordEntity> searchHits = elasticsearchTemplate.search(query, MarriageLawRecordEntity.class);
            List<MarriageLawRecordEntity> ms = searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
            return ms;
        });

        //关键字查询
        Future<List<MarriageLawRecordEntity>> keyword = executor.submit(() -> {
            NativeQuery query2 = NativeQuery.builder()
                    .withQuery(q -> q
                            .queryString(qs -> qs
                                    .fields("content", "chapterName", "item", "chapter")
                                    .query(context)))
                    .build();
            SearchHits<MarriageLawRecordEntity> searchHits2 = elasticsearchTemplate.search(query2, MarriageLawRecordEntity.class);
            List<MarriageLawRecordEntity> ms2 = searchHits2.getSearchHits().stream().map(SearchHit::getContent).toList();
            //取前10条
            if(ms2.size() > 10)
                ms2 = ms2.subList(0, 10);
            return ms2;
        });

        Set<MarriageLawRecordEntity> set = new LinkedHashSet<>();
        try {
            set.addAll(knn.get());
            set.addAll(keyword.get());
        }catch (ExecutionException | InterruptedException e){
            e.printStackTrace();
            LogUtils.log(MarriageLawServiceImpl.class, "KNN检索或关键字检索出错");
            return new ArrayList<>();
        }

        //rerank
        List<MarriageLawRecordEntity> sort = new ArrayList<>(set);
        List<String> documents = new ArrayList<>();
        for (MarriageLawRecordEntity ms1 : sort) {
            documents.add(ms1.getContent() + " " + ms1.getChapterName() + " " + ms1.getItem() + " " + ms1.getChapter());
        }
        List<MultiGroup> multiGroups = rerankUtils.rerank(context, documents, 10, threshold);

        List<LegalRecordEntity> result = new ArrayList<>();
        for (MultiGroup multiGroup : multiGroups) {
            MarriageLawRecordEntity marriageLawRecordEntity = sort.get(multiGroup.get(0));
            marriageLawRecordEntity.setVector(null);
            result.add(marriageLawRecordEntity);
        }
        //关闭线程池
        executor.shutdown();
        return result;
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new MarriageLawEntity();
    }
}
