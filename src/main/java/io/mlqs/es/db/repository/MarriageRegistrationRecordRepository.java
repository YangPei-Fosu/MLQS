package io.mlqs.es.db.repository;

import io.mlqs.es.db.MarriageRegistrationRecordEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarriageRegistrationRecordRepository extends ElasticsearchRepository<MarriageRegistrationRecordEntity, String> {

}
