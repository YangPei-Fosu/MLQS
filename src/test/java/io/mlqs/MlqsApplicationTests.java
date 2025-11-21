package io.mlqs;

import io.mlqs.es.legal.db.MarriageLawRecordEntity;
import io.mlqs.es.legal.db.MarriageRegistrationRecordEntity;
import io.mlqs.es.legal.db.repository.MarriageLawRecordRepository;
import io.mlqs.es.legal.db.repository.MarriageRegistrationRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MlqsApplicationTests {

    @Autowired
    private MarriageLawRecordRepository marriageLawRecordRepository;
    @Autowired
    private MarriageRegistrationRecordRepository marriageRegistrationRecordRepository;
    @Test
    void contextLoads() {

    }

}
