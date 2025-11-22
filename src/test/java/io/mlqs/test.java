package io.mlqs;

import io.mlqs.agent.Prompt;
import io.mlqs.es.DocumentService;
import io.mlqs.es.entity.DocumentSearchResultEntity;
import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.services.LegalService;
import io.mlqs.utils.EmbeddingUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MlqsApplication.class)
public class test {
    @Autowired
    private EmbeddingUtils embeddingUtils;
    @Autowired
    private DocumentService documentService;

    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;

    //检索
    @Test
    public void testMRRS() {
        String query = "离婚需要什么证件？";
        LegalService marriageRegistration = documentService.getLegalService("MarriageRegistration");
        List<LegalRecordEntity> knn = marriageRegistration.query(query, EmbeddingUtils.typeToFloat(embeddingUtils.toVector(query)),0.9);
        for (LegalRecordEntity record : knn) {
            System.out.println(record);
        }
    }

    //检索
    @Test
    public void testMR() {
        //输出的是集合是倒序的，关联度最高的在最下面（Index最大）
        DocumentSearchResultEntity documentSearchResultEntity = documentService.hybridSearch("结婚证申领", Map.of("MarriageRegistration", 0.3));
        System.out.println(documentSearchResultEntity);
    }

    //关键字匹配
    @Test
    public void test() {
        String context = "如何获得结婚证";
        NativeQuery query2 = NativeQuery.builder()
                .withQuery(q -> q
                        .queryString(qs -> qs
                                .fields("content", "chapterName", "item")
                                .query(context)))
                .build();
        SearchHits<MarriageRegistrationRecordEntity> searchHits2 = elasticsearchTemplate.search(query2, MarriageRegistrationRecordEntity.class);
        //输出
        for (SearchHit<MarriageRegistrationRecordEntity> hit : searchHits2.getSearchHits()) {
            System.out.println(hit.getContent().toStringWithoutVector());
        }
    }
    @Autowired
    private Prompt prompt;

    @Test
    public void test2() {
        String chatPrompt = prompt.getChatPrompt();
        System.out.println(chatPrompt);
    }
}
