package io.mlqs.es.db;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends ElasticsearchRepository<DocumentEntity, String> {
}
