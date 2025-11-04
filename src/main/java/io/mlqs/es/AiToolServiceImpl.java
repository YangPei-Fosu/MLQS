package io.mlqs.es;

import io.mlqs.es.db.DocumentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiToolServiceImpl implements AiToolService{
    @Autowired
    private DocumentService documentService;

    @Override
    public List<DocumentEntity> search(String context) {
        return documentService.hybridSearch(context);
    }
}
