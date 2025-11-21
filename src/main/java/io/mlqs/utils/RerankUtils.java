package io.mlqs.utils;

import com.google.gson.*;
import io.mlqs.utils.clazz.MultiGroup;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.mlqs.utils.EmbeddingUtils.JSON;

@Component
public class RerankUtils {
    @Value("${mlqs.rerank.url}")
    private String url;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * rerank重排列函数
     * @param query 查询字符串
     * @param documents 文档列表
     * @param topK 返回的文档数量
     * @param threshold 关联度阈值，默认值为0，只返回关联度大于阈值的文档，区间为[0,1]
     * @return 返回一个多元组列表[在documents内的下标，相关度，documents内的内容]
     */
    public List<MultiGroup> rerank(String query, List<String> documents, int topK, double threshold) {
        try {
            if (documents == null)
                throw new Exception("文档列表不能为null");
            if (topK <= 0)
                throw new Exception("topK不能小于等于0");
            if(threshold < 0 || threshold > 1)
                throw new Exception("关联度阈值范围必须在[0,1]内");

            List<MultiGroup> result = new ArrayList<>();
            LogUtils.debug(this.getClass(),"开始重排列，关联度阈值为"+ threshold);

            Gson gson = new Gson();
            String json = "{\"query\":\""+ query +"\", \"documents\": "+ gson.toJson(documents) +" }";
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String res = response.body().string();
            //处理返回结果
            JsonObject root = JsonParser.parseString(res).getAsJsonObject();
            JsonArray resultArray = root.getAsJsonArray("results");
            //取前topK个结果
            int top = 0;
            for (JsonElement je : resultArray.asList()) {
                int index = je.getAsJsonObject().get("index").getAsInt();
                Float relevance_score = je.getAsJsonObject().get("relevance_score").getAsFloat();
                //判断关联度是否大于阈值
                if (relevance_score >= threshold){
                    //大于阈值添加到结果列表中
                    result.add(MultiGroup.of(index, relevance_score, documents.get(index)));
                    //top数+1
                    top++;
                    //判断是否达到topK
                    if (top >= topK)
                        break;
                }
            }
            LogUtils.debug(this.getClass(),"重排列完成");
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
/**返回数据结构
 * {
 *   "id": "16dcfbbd-c6a9-11f0-8a83-68342120356d",
 *   "meta": {
 *     "api_version": null,
 *     "billed_units": null,
 *     "tokens": null,
 *     "warnings": null
 *   },
 *   "results": [
 *     {
 *       "document": null,
 *       "index": 0,
 *       "relevance_score": 0.9991115927696228
 *     },
 *     {
 *       "document": null,
 *       "index": 2,
 *       "relevance_score": 0.9959338307380676
 *     },
 *     {
 *       "document": null,
 *       "index": 4,
 *       "relevance_score": 0.9231637716293335
 *     },
 *     {
 *       "document": null,
 *       "index": 3,
 *       "relevance_score": 0.45634377002716064
 *     },
 *     {
 *       "document": null,
 *       "index": 1,
 *       "relevance_score": 0.00028497728635556996
 *     }
 *   ]
 * }
 */