package com.jpmc.midascore;

import com.jpmc.midascore.repository.UserRecordRepository;
import com.jpmc.midascore.entity.UserRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
public class TaskFourTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRecordRepository userRecordRepository;

    @Test
    void task_four_verifier() throws Exception {

        userPopulator.populate();

        String[] transactionLines =
                fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");

        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // WAIT for Kafka + Incentive API
        Thread.sleep(10000);

        UserRecord wilbur = userRecordRepository.findAll()
                .stream()
                .filter(u -> u.getName().equals("wilbur"))
                .findFirst()
                .orElseThrow();

        // FORCE Maven to show the value
        throw new RuntimeException(
                "FINAL WILBUR BALANCE = " + wilbur.getBalance()
        );
    }
}
