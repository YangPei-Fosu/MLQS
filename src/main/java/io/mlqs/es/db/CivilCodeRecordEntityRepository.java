package io.mlqs.es.db;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CivilCodeRecordEntityRepository extends ElasticsearchRepository<CivilCodeRecordEntity, String> {
}
