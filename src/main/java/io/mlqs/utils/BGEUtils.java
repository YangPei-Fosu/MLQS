package io.mlqs.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BGEUtils {
    @Value("${mlqs.bge.url}")
    private String url;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    //向量化字符串
    public double[] toVector(String context) {
        try {
            OkHttpClient client = new OkHttpClient();

            String json = "[{\"content\":\""+ context +"\", \"vector\":\"\"}]";
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
                JsonObject root = JsonParser.parseString(res).getAsJsonObject();
                JsonArray resultArray = root.getAsJsonArray("result");
                JsonObject firstItem = resultArray.get(0).getAsJsonObject();
                JsonArray vectorArray = firstItem.getAsJsonArray("vector");

                double[] vector = new double[vectorArray.size()];
                LogUtils.debug(this.getClass(),"开始转换");
                for (int i = 0; i < vectorArray.size(); i++) {
                    vector[i] = vectorArray.get(i).getAsDouble();
                }
                LogUtils.debug(this.getClass(),"转换成功");
                return vector;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
