package com.jpmc.midascore;

import com.jpmc.midascore.repository.UserRecordRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class TaskThreeTests {

    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    // ✅ CORRECT: access repository directly
    @Autowired
    private UserRecordRepository userRecordRepository;

    @Test
    void task_three_verifier() throws InterruptedException {

        // Step 1: Load users
        userPopulator.populate();

        // Step 2: Send transactions
        String[] transactionLines =
                fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");

        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        Thread.sleep(2000);

        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("----------------------------------------------------------");
        logger.info("WALDORF FINAL BALANCE (LOOK BELOW)");
        logger.info("----------------------------------------------------------");

        // Step 3: PRINT ALL USERS (INCLUDING WALDORF)
        userRecordRepository
                .findAll()
                .stream()
                .filter(u -> u.getName().equalsIgnoreCase("waldorf"))
                .forEach(u -> {
                    System.out.println("===================================");
                    System.out.println("WALDORF FINAL BALANCE = " + (int) u.getBalance());
                    System.out.println("===================================");
                });


        logger.info("kill this test once you find the answer");

        Thread.sleep(3000);
        System.exit(0);
    }
}
