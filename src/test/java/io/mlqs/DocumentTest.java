package io.mlqs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.mlqs.es.DocumentService;
import io.mlqs.es.legal.db.CivilCodeRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.db.repository.CivilCodeRecordRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
@SpringBootTest
@RunWith(SpringRunner.class)
public class DocumentTest {
    @Autowired
    private CivilCodeRecordRepository documentRepository;
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

    @Test
    public void insert2() {
        String filePath = "db/data/婚姻登记条例_formatted.json";
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
        System.out.println(json);
        List<MarriageRegistrationRecordEntity> marriageRegistrationRecordEntities = new ArrayList<>();
        Gson gson = new Gson();
        marriageRegistrationRecordEntities = gson.fromJson(json, new TypeToken<List<MarriageRegistrationRecordEntity>>(){}.getType());
        System.out.println(marriageRegistrationRecordEntities);

    }

    @Autowired
    private DocumentService documentService;
    @Test
    public void find() {
        System.out.println(documentService.knn("f**k work",List.of("CivilCode","MarriageRegistration")));
    }
}
