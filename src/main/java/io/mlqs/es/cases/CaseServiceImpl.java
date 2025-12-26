package io.mlqs.es.cases;

import io.mlqs.es.cases.db.CaseEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
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
import java.util.List;

@Service
public class CaseServiceImpl implements CaseService{
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private RerankUtils rerankUtils;

    @Override
    public List<CaseEntity> query(String context, List<Float> vector, double threshold) {
        //设置k
        int k = 7;
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .knn(kb -> kb
                                .field("full_text_vector")
                                .queryVector(vector)
                                .numCandidates(k * 2)))
                .withPageable(PageRequest.of(0, k))
                .build();
        //取top3
        SearchHits<CaseEntity> searchHits = elasticsearchTemplate.search(query, CaseEntity.class);
        List<CaseEntity> ms = searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
        List<String> documents = new ArrayList<>();
        for (CaseEntity ms1 : ms) {
            documents.add(ms1.getFull_text());
        }
        List<MultiGroup> result = rerankUtils.rerank(context, documents, 3, threshold);
        List<CaseEntity> ms2 = new ArrayList<>();
        for (MultiGroup multiGroup : result) {
            CaseEntity caseEntity = ms.get(multiGroup.get(0));
            caseEntity.setFull_text_vector(null);
            ms2.add(caseEntity);
        }
        return ms2;
    }
}
