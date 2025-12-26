package io.mlqs;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import io.mlqs.agent.Agent;
import io.mlqs.agent.Prompt;
import io.mlqs.es.AiToolService;
import io.mlqs.es.DocumentService;
import io.mlqs.es.cases.db.CaseEntity;
import io.mlqs.es.entity.DocumentSearchResultEntity;
import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.services.LegalService;
import io.mlqs.utils.EmbeddingUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
import java.io.IOException;
import java.lang.reflect.Field;
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

    @Autowired
    private AiToolService aiToolService;
    //检索
    @Test
    public void testMR() {
        //输出的是集合是倒序的，关联度最高的在最下面（Index最大）
        DocumentSearchResultEntity search = aiToolService.search("1", "对于结婚人员有什么要求呢？", Map.of("MarriageRegistration", 0.4, "CivilCode", 0.4, "MarriageLaw", 0.4));
        System.out.println(search);
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

    @Test
    public void test3() {
        String customUri = "http://159.75.85.114:9200";
        ElasticsearchTransport transport = new RestClientTransport(
                RestClient.builder(HttpHost.create(customUri)).build(),
                new JacksonJsonpMapper()
        );

        ElasticsearchClient client = new ElasticsearchClient(transport);

        try {
            // 使用 _stats API 获取所有索引的统计信息
            var statsResponse = client.indices().stats(r -> r
                    .index("*")  // 获取所有索引
            );

            // 获取索引名称集合
            var indices = statsResponse.indices().keySet();
            System.out.println("ES服务中总共有 " + indices.size() + " 个索引:");

            // 输出每个索引的文档总数
            for (String indexName : indices) {
                var indexStats = statsResponse.indices().get(indexName);
                var docsCount = indexStats.total().docs().count();

                System.out.println("索引名称: " + indexName +
                        ", 文档数量: " + docsCount );
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

    @Test
    public void testGetIndexCount() {
        Field[] fields = CaseEntity.class.getDeclaredFields();
        System.out.println(fields.length);
    }

    @Test
    public void test4() {
        String customUri = "http://159.75.85.114:9200";
        ElasticsearchTransport transport = new RestClientTransport(
                RestClient.builder(HttpHost.create(customUri)).build(),
                new JacksonJsonpMapper()
        );

        ElasticsearchClient client = new ElasticsearchClient(transport);

        try {
            // 使用布尔查询来查找包含婚姻、结婚、离婚等关键词的文档
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

            long totalHits = response.hits().total().value();
            System.out.println("找到 " + totalHits + " 条关于婚姻/结婚/离婚相关记录");

            // 输出前10条记录的详细信息
            int count = 0;
            for (Hit<CaseEntity> hit : response.hits().hits()) {
                if (count >= 10) break; // 只显示前10条详细信息

                CaseEntity caseEntity = hit.source();
                System.out.println("文档ID: " + caseEntity.getDoc_id());
                System.out.println("文档分数: " + hit.score());
                System.out.println("案件标题: " + caseEntity.getTitle());
                System.out.println("案件全文: " + caseEntity.getFull_text());
                System.out.println("---");
                count++;
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

    @Autowired
    private ChatLanguageModel model;

    @Test
    public void test5() {
        String CHAT_prompt = """
                记得测试一下，用大模型提问，系统回答，大模型打分的方式。
                prompt参考：
                **角色** \s
                你是一位婚姻法问答系统的质检员，任务是**先基于给定法律条文生成测试问题，待系统返回答案后，再从六个维度对答案进行 1–5 分打分**。
                                
                **输入** \s
                1. **法律条文**：`"
                {"content":{"CivilCode":{"records":[{"chapter":"二","item":"一千零四十八","content":"直系血亲或者三代以内的旁系血亲禁止结婚。"},{"chapter":"二","item":"一千零四十六","content":"结婚应当男女双方完全自愿，禁止任何一方对另一方加以强迫，禁止任何组织或者个人加以干涉。"},{"chapter":"二","item":"一千零四十九","content":"要求结婚的男女双方应当亲自到婚姻登记机关申请结婚登记。符合本法规定的，予以登记，发给结婚证。完成结婚登记，即确立婚姻关系。未办理结婚登记的，应当补办登记。"}],"extend":{}},"MarriageLaw":{"records":[{"chapter":"一","chapterName":"总则","item":"四","content":"第四条夫妻应当互相忠实，互相尊重；家庭成员间应当敬老爱幼，互相帮助，维护平等、和睦、文明的婚姻家庭关系。"},{"chapter":"二","chapterName":"结婚","item":"五","content":"第五条结婚必须男女双方完全自愿，不许任何一方对他方加以强迫或任何第三者加以干涉。"},{"chapter":"二","chapterName":"结婚","item":"八","content":"第八条要求结婚的男女双方必须亲自到婚姻登记机关进行结婚登记。符合本法规定的，予以登记，发给结婚证。取得结婚证，即确立夫妻关系。未办理结婚登记的，应当补办登记。"}],"extend":{}},"MarriageRegistration":{"records":[{"chapter":"第二章","chapterName":"结婚登记","item":"第十一条","content":"要求结婚的男女双方未办理结婚登记的，应当补办登记。男女双方补办结婚登记的，适用本条例结婚登记的规定。"},{"chapter":"第二章","chapterName":"结婚登记","item":"第十条","content":"婚姻登记机关应当核对结婚登记当事人出具的证件、书面材料，询问相关情况，并对当事人的身份以及婚姻状况信息进行联网核对，依法维护当事人的权益。对当事人符合结婚条件的，应当当场予以登记，发给结婚证；对当事人不符合结婚条件不予登记的，应当向当事人说明理由。"},{"chapter":"第二章","chapterName":"结婚登记","item":"第八条","content":"申请结婚登记的内地居民应当出具下列证件和书面材料：（一）本人的居民身份证；（二）本人无配偶以及与对方当事人没有直系血亲和三代以内旁系血亲关系的签字声明。申请结婚登记的香港居民、澳门居民、台湾居民应当出具下列证件和书面材料：（一）本人的有效通行证或者港澳台居民居住证、身份证；（二）经居住地公证机构公证的本人无配偶以及与对方当事人没有直系血亲和三代以内旁系血亲关系的声明。申请结婚登记的华侨应当出具下列证件和书面材料：（一）本人的有效护照；（二）居住国公证机构或者有权机关出具的、经中华人民共和国驻该国使（领）馆认证的本人无配偶以及与对方当事人没有直系血亲和三代以内旁系血亲关系的证明，或者中华人民共和国驻该国使（领）馆出具的本人无配偶以及与对方当事人没有直系血亲和三代以内旁系血亲关系的证明。中华人民共和国缔结或者参加的国际条约另有规定的，按照国际条约规定的证明手续办理。申请结婚登记的外国人应当出具下列证件和书面材料：（一）本人的有效护照或者其他有效的国际旅行证件，或者外国人永久居留身份证等中国政府主管机关签发的身份证件；（二）所在国公证机构或者有权机关出具的、经中华人民共和国驻该国使（领）馆认证或者该国驻华使（领）馆认证的本人无配偶的证明，或者所在国驻华使（领）馆出具的本人无配偶的证明。中华人民共和国缔结或者参加的国际条约另有规定的，按照国际条约规定的证明手续办理。申请结婚登记的当事人对外国主管机关依据本条第三款、第四款提及的国际条约出具的证明文书的真实性负责，并签署书面声明。"},{"chapter":"第一章","chapterName":"总则","item":"第六条","content":"婚姻登记机关从事婚姻登记的工作人员应当接受婚姻登记业务培训，依照有关规定经考核合格，方可从事婚姻登记工作。婚姻登记机关办理婚姻登记，不得收取费用。婚姻登记机关及其工作人员在婚姻登记工作中发现疑似被拐卖、绑架的妇女的，应当依法及时向有关部门报告；发现当事人遭受家庭暴力或者面临家庭暴力的现实危险的，应当及时劝阻并告知受害人寻求救助的途径。婚姻登记机关及其工作人员应当对在婚姻登记工作中知悉的个人隐私、个人信息予以保密，不得泄露或者向他人非法提供。"}],"extend":{}}},"docNames":["CivilCode","MarriageLaw","MarriageRegistration"],"caseList":[]}
                                
                " \s
                2. 用户问题：
                对于结婚人员有什么要求呢？
                                
                3.AI回答：
                根据《中华人民共和国民法典》第一千零四十九条以及《中华人民共和国婚姻法》第八条的规定，对于结婚人员的要求主要包括：
                                
                双方必须是异性：现行法律框架下，要求结婚的双方为一男一女。
                自愿原则：男女双方必须出于完全自愿，不得有任何强迫或欺诈行为。
                亲自到场登记：要求结婚的男女双方应当亲自到婚姻登记机关申请结婚登记。这意味着不能由他人代理完成这一过程。
                符合法定条件：包括但不限于年龄（男性不得早于22周岁，女性不得早于20周岁）、无重婚情形、非直系血亲和三代以内旁系血亲等。
                如果满足上述条件，并且经过婚姻登记机关审核确认符合条件后，将予以登记并发给结婚证，从而正式确立夫妻关系。未办理结婚登记但已以夫妻名义共同生活的，应当按照法律规定补办登记手续。
                                
                以上信息基于《中华人民共和国民法典》(第一千零四十九条)及《中华人民共和国婚姻法》(第八条)的相关规定。如果您还有其他关于婚姻方面的疑问，请继续提问！
                                
                                
                **流程** \s
                对答案按以下六个维度逐项评分（每项 1–5 分，5 为最高）。
                                
                **打分维度（逐项输出）**
                                
                | 维度 | 评分标准（1→5） | 关键词 |
                | :--- | :--- | :--- |
                | **语言准确性** | 语法、拼写、标点、术语、事实零错误；每处错误扣 1 分。 | 零错误 |
                | **内容相关性** | 完全紧扣用户 prompt，无跑题、无冗余、不引用无关片段；偏离则 1–3 分。 | 无冗余 |
                | **逻辑连贯性** | 段落衔接自然、因果链条完整、论证逻辑闭环；跳跃或矛盾判 1–2 分。 | 因果闭环 |
                | **风格适配性** | 语体（书面/口语）、用词、句式与法律咨询场景严格匹配；风格错乱 ≤2 分。 | 法律文体 |
                | **事实一致性** | 与源文/检索片段零冲突、无幻觉；每处事实误差扣 2 分。 | 零幻觉 |
                | **结构合理性** | 标题层级清晰、分段合理、重点前置；结构混乱则 1–2 分。 | 重点突出 |
                                
                **输出格式（JSON）**
                                
                ```json
                {
                  "测试问题": "……",
                  "维度评分": {
                    "语言准确性": 5,
                    "内容相关性": 4,
                    "逻辑连贯性": 5,
                    "风格适配性": 5,
                    "事实一致性": 5,
                    "结构合理性": 4
                  },
                  "扣分说明": {
                    "语言准确性": "术语「抚养权」误写为「扶养权」",
                    "内容相关性": "末尾多了一句与问题无关的普法建议"
                  },
                  "综合得分": 4.5,
                  "是否通过": true
                }
                ```
                                
                **综合得分** = 六项平均分，≥4.0 为通过。 \s
                **扣分说明**：仅当某项评分≤3 时必填，指出具体问题。
                                
                """;

    }

    @Test
    public void test6() {
        String question = "如果获取结婚证？";
        System.out.println(getAIReturn(question));
    }

    String getAIReturn(String question){
        Prompt prompt = new Prompt();
        //获取聊天大模型
        Agent build = AiServices.builder(Agent.class)
                //模型
                .chatLanguageModel(model)
                //prompt
                .systemMessageProvider(mem -> prompt.getChatPrompt())
                //工具
                .tools(aiToolService)
                .build();
        return build.chat("1", question);
    }
}
