package io.mlqs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MarkerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = MlqsApplication.class)
@Slf4j
public class testLog {
    @Test
    public void test() {
        log.info(MarkerFactory.getMarker("TEST"),"{}", "hello world");
    }
}
