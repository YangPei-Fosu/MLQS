package io.mlqs.es.cases;

import io.mlqs.es.cases.entity.CaseEntity;

import java.util.List;

public interface CaseService {
    List<CaseEntity> knn(String context);
}
