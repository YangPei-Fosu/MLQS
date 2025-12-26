package io.mlqs.es.cases.db;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;


@NoArgsConstructor
@Data
@Document(indexName = "bas_case", createIndex = true)
public class CaseEntity {
/*
文档内容: {title=陈鹏与庾海莹婚姻家庭纠纷执行裁定书
court=陕西省合阳县人民法院
court_level=R5A
doc_id=321e184f551b483d88c5ac9c0117ee5d
version=02
case_number=（2020）陕0524执63号之一
case_type=执行案件
trial_level=执行实施
charges=[]
defendants=[庾海莹]
document_header=陕西省合阳县人民法院执行裁定书（2020）陕0524执63号之一
basic_info=本院依据已经发生法律效力的合阳县人民法院（2020）陕0524执63号之二裁定书，2020年03月31日向被执行人庾海莹发出执行通知书，责令被执行人接到通知后日内履行上述法律文书确定的义务，但被执行人庾海莹至今未履行。依照《中华人民共和国民事诉讼
prosecution_facts=法》第二百四十二条的规定，裁定如下
judgment_result=扣划被执行人庾海莹在中国邮政储蓄银行6217997900104459706账户内存款人民币3650.00元。本裁定立即执行
signature_info=审判员雷宏二〇二〇年七月二十一日书记员王富民
filing_date=2020-07-21
judgment_date=2020-12-28
related_cases=[]
applicable_laws=[]
legal_provisions=附相关法律条文：《中华人民共和国民事诉讼法》第二百四十二条被执行人未按执行通知书履行法律文书确定的义务，人民法院有权向有关单位查询被执行人的存款、债券、股票、基金份额等财产情况。人民法院有权根据不同情形扣押、冻结、划拨、变价被执行人财产。人民法院查询、扣押、冻结、划拨、变价的财产不得超出被执行人应当履行义务的范围。人民法院决定扣押、冻结、划拨、变价财产，应当作出裁定，并发出协助执行通知书，有关单位必须办理。第二百四十四条被执行人未按执行通知书履行法律文书确定的义务，人民法院有权查封、扣押、冻结、拍卖、变卖被执行人应当履行义务部分的财产。但应当保留被执行人及其所养家属的生活必需品。采取前款措施，人民法院应当作出裁定。联系人：王富民联系电话：5525304
full_text=陕西省合阳县人民法院执 行 裁 定 书(2020)陕0524执63号之一被执行人庾海莹，女性，住甘井镇。本院依据已经发生法律效力的合阳县人民法院(2020)陕0524执63号之二裁定书，2020年03月31日向被执行人庾海莹发出执行通知书，责令被执行人接到通知后日内履行上述法律文书确定的义务，但被执行人庾海莹至今未履行。依照《中华人民共和国民事诉讼法》第二百四十二条的规定，裁定如下：扣划被执行人庾海莹在中国邮政储蓄银行6217997900104459706账户内存款人民币3650.00元。本裁定立即执行。审判员 雷 宏二〇二〇年七月二十一日书记员 王富民附相关法律条文：《中华人民共和国民事诉讼法》第二百四十二条被执行人未按执行通知书履行法律文书确定的义务，人民法院有权向有关单位查询被执行人的存款、债券、股票、基金份额等财产情况。人民法院有权根据不同情形扣押、冻结、划拨、变价被执行人财产。人民法院查询、扣押、冻结、划拨、变价的财产不得超出被执行人应当履行义务的范围。人民法院决定扣押、冻结、划拨、变价财产，应当作出裁定，并发出协助执行通知书，有关单位必须办理。第二百四十四条被执行人未按执行通知书履行法律文书确定的义务，人民法院有权查封、扣押、冻结、拍卖、变卖被执行人应当履行义务部分的财产。但应当保留被执行人及其所养家属的生活必需品。采取前款措施，人民法院应当作出裁定。联系人：王富民联系电话：552****
charge_details=[]
related_documents=[]
structure=[2,2,4,2,7]
network_source=outer
view_count=92
metadata={file_path=D:\裁判文书网文书\text\321e184f551b483d88c5ac9c0117ee5d.txt
file_name=321e184f551b483d88c5ac9c0117ee5d.txt
file_size=6555
index_time=2025-12-25T21:06:26.958272}}
 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String court;
    @Field(type = FieldType.Keyword)
    private String court_level;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String court_opinion;
    @Id
    @Field(type = FieldType.Keyword)
    private String doc_id;
    @Field(type = FieldType.Keyword)
    private String version;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String case_number;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String case_type;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String trial_level;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private List<String> charges;
    @Field(type = FieldType.Text)
    private List<String> defendants;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String document_header;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String basic_info;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String prosecution_facts;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String judgment_result;
    @Field(type = FieldType.Text)
    private String signature_info;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Field(type = FieldType.Date)
    private Date filing_date;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Field(type = FieldType.Date)
    private Date judgment_date;
    @Field(type = FieldType.Text)
    private List<String> related_cases;
    @Field(type = FieldType.Text)
    private List<String> applicable_laws;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String legal_provisions;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String full_text;
    /**
     * 全文向量字段
     */
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private double[] full_text_vector;
    @Field(type = FieldType.Text)
    private List<String> charge_details;
    @Field(type = FieldType.Text)
    private List<String> related_documents;
    @Field(type = FieldType.Integer)
    private List<Integer> structure;
    @Field(type = FieldType.Text)
    private String network_source;
    @Field(type = FieldType.Integer)
    private Integer view_count;
    @Field(type = FieldType.Object)
    private Map<String, Object> metadata;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
