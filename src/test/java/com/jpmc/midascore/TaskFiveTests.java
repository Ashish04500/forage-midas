package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Balance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
public class TaskFiveTests {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private BalanceQuerier balanceQuerier;

    @Test
    void task_five_verifier() throws InterruptedException {
        userPopulator.populate();

        String[] transactionLines =
                fileLoader.loadStrings("/test_data/rueiwoqp.tyruei");

        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        Thread.sleep(2000);

        StringBuilder output = new StringBuilder();
        output.append("\n---begin output ---\n");

        for (int i = 0; i < 13; i++) {
            Balance balance = balanceQuerier.query((long) i);
            output.append(balance.toString()).append("\n");
        }

        output.append("---end output ---");

        // ✅ THIS WILL PRINT
        System.out.println(output.toString());

        // ❌ DO NOT EXIT JVM
    }
}
