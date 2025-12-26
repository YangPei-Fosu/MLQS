package io.mlqs.es.cases.db;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends ElasticsearchRepository<CaseEntity, String> {
}
