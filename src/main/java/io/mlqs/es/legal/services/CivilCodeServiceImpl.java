package io.mlqs.es.legal.services;

import io.mlqs.es.legal.db.CivilCodeRecordEntity;
import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.db.MarriageLawRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.entity.CivilCodeEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import io.mlqs.utils.LogUtils;
import io.mlqs.utils.RerankUtils;
import io.mlqs.utils.clazz.MultiGroup;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class CivilCodeServiceImpl implements LegalService {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private RerankUtils rerankUtils;

    @Override
    public List<LegalRecordEntity> query(String context, List<Float> vector, double threshold) {
        //创建两个子线程，分别完成关键字查询和knn查询
        ExecutorService executor = Executors.newFixedThreadPool(2);

        //knn查询
        Future<List<CivilCodeRecordEntity>> knn = executor.submit(() -> {
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
            SearchHits<CivilCodeRecordEntity> searchHits = elasticsearchTemplate.search(query, CivilCodeRecordEntity.class);
            List<CivilCodeRecordEntity> ms = searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
            return ms;
        });

        //关键字查询
        Future<List<CivilCodeRecordEntity>> keyword = executor.submit(() -> {
            NativeQuery query2 = NativeQuery.builder()
                    .withQuery(q -> q
                            .queryString(qs -> qs
                                    .fields("code","codeName", "chapter", "chapterName", "section", "sectionName", "item", "content")
                                    .query(context)))
                    .build();
            SearchHits<CivilCodeRecordEntity> searchHits2 = elasticsearchTemplate.search(query2, CivilCodeRecordEntity.class);
            List<CivilCodeRecordEntity> ms2 = searchHits2.getSearchHits().stream().map(SearchHit::getContent).toList();
            //取前15条
            if(ms2.size() > 10)
                ms2 = ms2.subList(0, 10);
            return ms2;
        });

       //合并去重结果集
        Set<CivilCodeRecordEntity> mixSet = new LinkedHashSet<>();
        try {
            mixSet.addAll(knn.get());
            mixSet.addAll(keyword.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            LogUtils.log(CivilCodeServiceImpl.class, "KNN检索或关键字检索出错");
            return new ArrayList<>();
        }
        //Set转为List
        List<CivilCodeRecordEntity> ms = new ArrayList<>(mixSet);
        
        //构建查询文档
        List<String> documents = new ArrayList<>();
        for (CivilCodeRecordEntity ms1 : ms) {
            documents.add(ms1.getCode() + " " 
                    + ms1.getCodeName() + " " 
                    + ms1.getChapter() + " " 
                    + ms1.getChapterName() + " " 
                    + ms1.getSection() + " " 
                    + ms1.getSectionName() + " " 
                    + ms1.getItem() + " " + ms1.getContent());
        }
        List<MultiGroup> rerank = rerankUtils.rerank(context, documents, 10, threshold);
        //返回集合
        List<LegalRecordEntity> result = new ArrayList<>();
        for (MultiGroup multiGroup : rerank) {
            CivilCodeRecordEntity civilCodeRecordEntity = ms.get(multiGroup.get(0));
            civilCodeRecordEntity.setVector(null);
            result.add(civilCodeRecordEntity);
        }
        executor.shutdown();
        return result;
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new CivilCodeEntity();
    }
}
