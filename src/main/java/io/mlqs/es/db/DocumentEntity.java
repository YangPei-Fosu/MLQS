package io.mlqs.es.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "bas_legal", createIndex = true)
public class DocumentEntity {
    @Id
    private String id;
}
