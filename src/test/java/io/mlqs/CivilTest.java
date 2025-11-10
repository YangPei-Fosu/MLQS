package io.mlqs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.mlqs.es.db.CivilCodeRecordEntity;
import io.mlqs.es.db.CivilCodeRecordEntityRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CivilTest {
    @Autowired
    private CivilCodeRecordEntityRepository documentRepository;
    @Test
    public void insert() {
        //读入文件
        String filePath = "db/data/民法典_formatted.json";
        File file = new File(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        //读取文件
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = stringBuilder.toString();
        json = json.replaceAll("id","code");
        List<CivilCodeRecordEntity> civilCodeRecordEntities = new ArrayList<>();
        Gson gson = new Gson();
        civilCodeRecordEntities = gson.fromJson(json, new TypeToken<List<CivilCodeRecordEntity>>(){}.getType());
        System.out.println(civilCodeRecordEntities);
    }
}
