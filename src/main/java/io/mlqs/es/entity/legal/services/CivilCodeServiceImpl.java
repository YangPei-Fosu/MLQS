package io.mlqs.es.entity.legal.services;

import io.mlqs.es.db.LegalRegulationsEntity;
import io.mlqs.es.entity.legal.CivilCodeEntity;
import io.mlqs.es.entity.legal.LegalEntity;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class CivilCodeServiceImpl implements LegalService {
    @Override
    public List<LegalRegulationsEntity> knn(String context) {
        return new ArrayList<>();
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new CivilCodeEntity();
    }
}
