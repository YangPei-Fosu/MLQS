package io.mlqs.es;

import io.mlqs.es.entity.DocumentSearchResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiToolServiceImpl implements AiToolService{
    @Autowired
    private DocumentService documentService;

    @Override
    public List<DocumentSearchResultEntity> search(String context) {
        //test
        return new ArrayList<>();
//        return documentService.hybridSearch(context);
    }
}
