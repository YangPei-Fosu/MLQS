package io.mlqs.es.cases;

import io.mlqs.es.cases.db.CaseEntity;

import java.util.List;

public interface CaseService {
    List<CaseEntity> query(String context, List<Float> vector, double threshold);
}
