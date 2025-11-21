package io.mlqs.es.legal.services;

import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import io.mlqs.es.legal.entity.MarriageRegistrationEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarriageRegistrationServiceImpl implements LegalService{
    @Override
    public List<LegalRecordEntity> knn(String context, List<Float> vector) {
        return new ArrayList<>();
    }

    @Override
    public LegalEntity getLegalEntity() {
        return new MarriageRegistrationEntity();
    }
}
