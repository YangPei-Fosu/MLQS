package io.mlqs.es;

import io.mlqs.es.entity.DocumentSearchResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiToolServiceImpl implements AiToolService{
    @Autowired
    private DocumentService documentService;

    @Override
    public DocumentSearchResultEntity search(String context, List<String> legalName) {
        return documentService.hybridSearch(context, legalName);
    }
}
