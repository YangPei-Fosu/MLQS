package io.mlqs.es.legal.entity.services;

import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.entity.CivilCodeEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class CivilCodeServiceImpl implements LegalService {
    @Override
    public List<LegalRecordEntity> knn(String context) {
        return new ArrayList<>();
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new CivilCodeEntity();
    }
}
