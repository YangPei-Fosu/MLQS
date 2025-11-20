package io.mlqs.es.legal.db.repository;

import io.mlqs.es.legal.db.MarriageLawRecordEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarriageLawRecordRepository extends ElasticsearchRepository<MarriageLawRecordEntity, String> {
}
