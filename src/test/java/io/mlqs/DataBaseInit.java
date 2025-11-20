package io.mlqs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.mlqs.es.legal.db.CivilCodeRecordEntity;
import io.mlqs.es.legal.db.MarriageLawRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.db.repository.CivilCodeRecordRepository;
import io.mlqs.es.legal.db.repository.MarriageLawRecordRepository;
import io.mlqs.es.legal.db.repository.MarriageRegistrationRecordRepository;
import io.mlqs.utils.BGEUtils;
import io.mlqs.utils.LogUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MlqsApplication.class)
public class DataBaseInit {
    @Autowired
    private BGEUtils bgeUtils;
    @Autowired
    private MarriageRegistrationRecordRepository marriageRegistrationRecordRepository;
    @Autowired
    private CivilCodeRecordRepository civilCodeRecordRepository;
    @Autowired
    private MarriageLawRecordRepository marriageLawRecordRepository;

    @Resource
    private ElasticsearchTemplate esTemp;

    @Test
    public void main() {
        if (esTemp.indexOps(MarriageRegistrationRecordEntity.class).exists())
            esTemp.indexOps(MarriageRegistrationRecordEntity.class).delete();
        esTemp.indexOps(MarriageRegistrationRecordEntity.class).create();
        init();
        if(esTemp.indexOps(CivilCodeRecordEntity.class).exists())
            esTemp.indexOps(CivilCodeRecordEntity.class).delete();
        esTemp.indexOps(CivilCodeRecordEntity.class).create();
        init2();
        if(esTemp.indexOps(MarriageLawRecordEntity.class).exists())
            esTemp.indexOps(MarriageLawRecordEntity.class).delete();
        esTemp.indexOps(MarriageLawRecordEntity.class).create();
        init3();
    }

    @Test
    public void init() {
        LogUtils.debug(this.getClass(), "婚姻登记条例初始化中...");
        String filePath = "db/data/婚姻登记条例.json";
        File file = new File(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        //读取文件
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = stringBuilder.toString();
        List<MarriageRegistrationRecordEntity> marriageRegistrationRecordEntities;
        Gson gson = new Gson();
        marriageRegistrationRecordEntities = gson.fromJson(json, new TypeToken<List<MarriageRegistrationRecordEntity>>(){}.getType());
        LogUtils.Bar bar = LogUtils.progress(this.getClass(), "向量化字符串", 0, marriageRegistrationRecordEntities.size());
        bar.start();
        marriageRegistrationRecordEntities.forEach(entity -> {
            bar.update(1);
            String s = entity.getChapter() + entity.getChapterName() + entity.getItem() + entity.getContent();
            entity.setVector(bgeUtils.toVector(s));
        });
//        marriageRegistrationRecordRepository.saveAll(marriageRegistrationRecordEntities);
        LogUtils.debug(this.getClass(), "婚姻登记条例初始化完成...");
    }

    @Test
    public void init2() {
        LogUtils.debug(this.getClass(), "民法典初始化中...");
        String filePath = "db/data/民法典.json";
        File file = new File(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        String json = stringBuilder.toString();
        List<CivilCodeRecordEntity> civilCodeRecordEntities;
        Gson gson = new Gson();
        civilCodeRecordEntities = gson.fromJson(json, new TypeToken<List<CivilCodeRecordEntity>>(){}.getType());
        LogUtils.Bar bar = LogUtils.progress(this.getClass(), "向量化字符串", 0, civilCodeRecordEntities.size());
        bar.start();
        civilCodeRecordEntities.forEach(entity -> {
            bar.update(1);
            String s = entity.getCode() + entity.getCodeName() + entity.getChapter() + entity.getChapterName() + entity.getSection() + entity.getSectionName() + entity.getItem() + entity.getContent();
            entity.setVector(bgeUtils.toVector(s));
        });
//        civilCodeRecordRepository.saveAll(civilCodeRecordEntities);
        LogUtils.debug(this.getClass(), "民法典初始化完成...");
    }

    @Test
    public void init3() {
        LogUtils.debug(this.getClass(), "婚姻法初始化中...");
        String filePath = "db/data/婚姻法.json";
        File file = new File(filePath);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = stringBuilder.toString();
        List<MarriageLawRecordEntity> marriageLawRecordEntities;
        Gson gson = new Gson();
        marriageLawRecordEntities = gson.fromJson(json, new TypeToken<List<MarriageLawRecordEntity>>(){}.getType());
        LogUtils.Bar bar = LogUtils.progress(this.getClass(), "向量化字符串", 0, marriageLawRecordEntities.size());
        bar.start();
        marriageLawRecordEntities.forEach(entity -> {
            bar.update(1);
            String s = entity.getChapter() + entity.getChapterName() + entity.getItem() + entity.getContent();
            entity.setVector(bgeUtils.toVector(s));
        });
//        marriageLawRecordRepository.saveAll(marriageLawRecordEntities);
        LogUtils.debug(this.getClass(), " 婚姻法初始化完成...");
    }
}
