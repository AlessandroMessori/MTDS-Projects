package it.polimi.middleware.kafka.noise;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class MqttKafkaBridge implements MqttCallback {

    private static final String mqttTopic = "iot/native/launchpad/json";
    private static final String mqttServerAddr = "tcp://[fd00::1]:1883";

    private static final String kafkaTopic = "raw_noise_readings";
    private static final String kafkaServerAddr = "localhost:9092";
    private static final boolean kafkaWaitAck = true;

    MqttClient client;
    KafkaProducer<String, String> kafkaProducer;

    public MqttKafkaBridge() {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerAddr);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        kafkaProducer = new KafkaProducer<>(props);
    }

    public static void main(String[] args) {
        new MqttKafkaBridge().start();
    }

    public void start() {

        try {
            client = new MqttClient(mqttServerAddr, "Sending");
            client.connect();
            client.setCallback(this);
            client.subscribe(mqttTopic);
            System.out.println("Starting Bridge");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        kafkaProducer.close();
        System.out.println("Lost Connection");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println(topic);
        System.out.println(message.toString());

        final String key = "Key";
        final String value = message.toString().toUpperCase();

        final ProducerRecord<String, String> record = new ProducerRecord<>(kafkaTopic, key, value);
        final Future<RecordMetadata> future = kafkaProducer.send(record);

        if (kafkaWaitAck) {
            try {
                RecordMetadata ack = future.get();
                System.out.println(
                        "Ack for topic " + ack.topic() + ", partition " + ack.partition() + ", offset " + ack.offset());
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery Complete");
    }

}