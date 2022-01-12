import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);
        for (int i=0;i<10;i++) {

            //Create Producer Properties
            Properties properties = new Properties();
            String bootstrapServers = "127.0.0.1:9092";
            properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            //Create the Producer
            KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

            //Create a producer record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", "hello_world "+Integer.toString(i));

            //send data
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        //record was successfully processed
                        logger.info("Received new metadata. \n" + "Topic: " + recordMetadata.topic() + "\n" + "Partition: "
                                + recordMetadata.partition() + "\n" + "Offsets: " + recordMetadata.offset() + "\n" + "TimeStamps: "
                                + recordMetadata.timestamp() + "\n");
                    } else {
                        logger.error("Error while Producing", e);
                    }
                }
            });
            //flush data
            producer.flush();
            //flush and close producer
            producer.close();
        }
    }
}
