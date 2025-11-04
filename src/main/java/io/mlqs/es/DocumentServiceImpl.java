package io.mlqs.es;
import io.mlqs.es.db.DocumentEntity;
import io.mlqs.es.db.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService{
    @Autowired
    private DocumentRepository documentRepository;

    //ES的Knn向量化检索
    @Override
    public List<DocumentEntity> knn(String context) {
        return null;
    }

    //Knn+关键字搜索
    @Override
    public List<DocumentEntity> hybridSearch(String context) {
        return null;
    }

    //保存一条记录
    @Override
    public void save(DocumentEntity documentEntity) {
        documentRepository.save(documentEntity);
    }

    @Override
    public void save(List<DocumentEntity> documentEntityList) {
        documentEntityList.forEach(documentEntity -> documentRepository.save(documentEntity));
    }
}
