package io.mlqs.es.legal.services;

import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.db.MarriageLawRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import io.mlqs.es.legal.entity.MarriageLawEntity;
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

@Service
public class MarriageLawServiceImpl implements LegalService{
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private RerankUtils rerankUtils;

    @Override
    public List<LegalRecordEntity> query(String context, List<Float> vector, double threshold) {
        //knn查询
        int k = 20;
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
        //knn重排
        List<String> documents = new ArrayList<>();
        for (MarriageLawRecordEntity m : ms)
            documents.add(m.getChapter() + m.getChapterName() + m.getItem() + m.getContent());
        List<MultiGroup> knn_rerank = rerankUtils.rerank(context, documents, 10, threshold);

        //关键字查询
        NativeQuery query2 = NativeQuery.builder()
                .withQuery(q -> q
                        .queryString(qs -> qs
                                .fields("content", "chapterName", "item", "chapter")
                                .query(context)))
                .build();
        SearchHits<MarriageLawRecordEntity> searchHits2 = elasticsearchTemplate.search(query2, MarriageLawRecordEntity.class);
        List<MarriageLawRecordEntity> ms2 = searchHits2.getSearchHits().stream().map(SearchHit::getContent).toList();
        //取前15条
        if(ms2.size() > 15)
            ms2 = ms2.subList(0, 15);
        //关键字重排
        List<String> documents2 = new ArrayList<>();
        for (MarriageLawRecordEntity m : ms2)
            documents2.add(m.getChapter() + m.getChapterName() + m.getItem() + m.getContent());
        List<MultiGroup> keyword_rerank = rerankUtils.rerank(context, documents2, 5, threshold);

        //合并rerank结果并去重，使用LinkedHashSet确保顺序
        Set<MarriageLawRecordEntity> ms3 = new LinkedHashSet<>();
        Set<String> documents3 = new LinkedHashSet<>();
        for (MultiGroup mg : knn_rerank) {
            ms3.add(ms.get(mg.get(0)));
            documents3.add(mg.get(2));
        }
        for (MultiGroup mg : keyword_rerank) {
            ms3.add(ms2.get(mg.get(0)));
            documents3.add(mg.get(2));
        }

        //最后对得到的documents3再次rerank
        List<MultiGroup> final_rerank = rerankUtils.rerank(context, documents3.stream().toList(), 5, threshold);
        List<LegalRecordEntity> final_ms = new ArrayList<>();
        List<MarriageLawRecordEntity> ms4 = ms3.stream().toList();
        for (MultiGroup mg : final_rerank){
            MarriageLawRecordEntity me = ms4.get(mg.get(0));
            me.setVector(null);
            final_ms.add(me);
        }
        return final_ms;
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new MarriageLawEntity();
    }
}
