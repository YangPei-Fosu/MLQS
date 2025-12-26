package io.mlqs;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.mlqs.es.cases.db.CaseRepository;
import io.mlqs.es.cases.db.CaseEntity;
import io.mlqs.utils.EmbeddingUtils;
import io.mlqs.utils.LogUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MlqsApplication.class)
public class CaseInit {
    @Autowired
    private EmbeddingUtils embeddingUtils;
    @Autowired
    private CaseRepository caseRepository;

    @Resource
    private ElasticsearchTemplate esTemp;

    @Test
    public void main() {
        LogUtils.log(this.getClass(), "案件数据初始化中...");
        if (esTemp.indexOps(CaseEntity.class).exists())
            esTemp.indexOps(CaseEntity.class).delete();
        esTemp.indexOps(CaseEntity.class).create();
        test3();
        LogUtils.log(this.getClass(), "案件数据初始化完成...");
    }


    @Test
    public void test3() {
        String customUri = "http://159.75.85.114:9200";
        ElasticsearchTransport transport = new RestClientTransport(
                RestClient.builder(HttpHost.create(customUri)).build(),
                new JacksonJsonpMapper()
        );

        ElasticsearchClient client = new ElasticsearchClient(transport);

        try {
            SearchResponse<CaseEntity> response = client.search(s -> s
                            .query(q -> q
                                    .bool(b -> b
                                            .should(should -> should
                                                    .multiMatch(mm -> mm
                                                            .fields("title", "full_text", "content")
                                                            .query("婚姻")
                                                    )
                                            )
                                            .should(should -> should
                                                    .multiMatch(mm -> mm
                                                            .fields("title", "full_text", "content")
                                                            .query("结婚")
                                                    )
                                            )
                                            .should(should -> should
                                                    .multiMatch(mm -> mm
                                                            .fields("title", "full_text", "content")
                                                            .query("离婚")
                                                    )
                                            )
                                    )
                            )
                            .size(10000), // 设置返回最大文档数量
                    CaseEntity.class
            );

            System.out.println("找到 " + response.hits().hits().size() + " 条婚姻相关记录");
            for (Hit<CaseEntity> hit : response.hits().hits()) {
                CaseEntity caseEntity = hit.source();
                caseEntity.setFull_text_vector(embeddingUtils.toVector(caseEntity.getFull_text()));
                caseRepository.save(caseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                transport.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
