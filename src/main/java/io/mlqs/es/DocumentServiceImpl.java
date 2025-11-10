package io.mlqs.es;
import io.mlqs.es.db.CivilCodeRecordEntityRepository;
import io.mlqs.es.entity.DocumentSearchResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService{
    @Autowired
    private CivilCodeRecordEntityRepository documentRepository;

    //ES的Knn向量化检索
    @Override
    public List<DocumentSearchResultEntity> knn(String context) {
        return null;
    }

    //Knn+关键字搜索
    @Override
    public List<DocumentSearchResultEntity> hybridSearch(String context) {
        return null;
    }
}
