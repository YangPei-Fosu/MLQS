package io.mlqs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.mlqs.agent.Prompt;
import io.mlqs.es.DocumentService;
import io.mlqs.es.legal.db.CivilCodeRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.db.repository.CivilCodeRecordRepository;
import io.mlqs.utils.BGEUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MlqsApplication.class)
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
        String filePath = "db/data/婚姻登记条例.json";
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
        List<MarriageRegistrationRecordEntity> marriageRegistrationRecordEntities;
        Gson gson = new Gson();
        marriageRegistrationRecordEntities = gson.fromJson(json, new TypeToken<List<MarriageRegistrationRecordEntity>>(){}.getType());
        System.out.println(marriageRegistrationRecordEntities.size());

    }

    @Autowired
    private DocumentService documentService;
    @Test
    public void find() {
        System.out.println(documentService.knn("f**k work",List.of("CivilCode","MarriageRegistration")));
    }

    @Autowired
    private Prompt prompt;
    @Test
    public void find2() {
        System.out.println(prompt.getChatPrompt());
    }

    @Autowired
    BGEUtils bgeUtils;
    @Test
    public void bgetest() {
        double[] res = bgeUtils.toVector("你好");
        System.out.println(Arrays.toString(res));
    }
}

