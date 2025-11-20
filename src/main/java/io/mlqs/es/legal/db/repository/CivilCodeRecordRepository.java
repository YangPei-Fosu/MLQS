package io.mlqs.es.legal.db.repository;

import io.mlqs.es.legal.db.CivilCodeRecordEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CivilCodeRecordRepository extends ElasticsearchRepository<CivilCodeRecordEntity, String> {

}
