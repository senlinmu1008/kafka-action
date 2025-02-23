package net.zhaoxiaobin.kafkaclickhouse;

import com.alibaba.fastjson2.JSON;
import net.zhaoxiaobin.kafkaclickhouse.entity.dto.IndicatorDataDTO;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.math.BigDecimal;
import java.util.Properties;

/**
 * @author zhaoxb
 * @date 2025-02-23 19:49
 */
public class WukongyaKafkaProduceTests {

    public static void main(String[] args) {
        // Kafka 配置
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "111.229.66.196:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 创建 Kafka 生产者
        Producer<String, String> producer = new KafkaProducer<>(props);
        // Topic 名称
        String topic = "indicatorDataSync";
        // 创建消息对象
        IndicatorDataDTO indicatorDataDTO = new IndicatorDataDTO();
        indicatorDataDTO.setIndicatorCode("CODE-001");
        indicatorDataDTO.setIndicatorValue(new BigDecimal("1234.5678"));
        indicatorDataDTO.setTime("20231001120000");
        indicatorDataDTO.setUpdateTime("2023-10-01 12:05:00");
        indicatorDataDTO.setAssetId("ASSET-001");
        indicatorDataDTO.setPeriodicity("D");
        try {
            String jsonMessage = JSON.toJSONString(indicatorDataDTO);
            // 创建 Kafka 消息记录
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, jsonMessage);
            // 发送消息
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("Message sent successfully! Topic: " + metadata.topic() +
                            ", Partition: " + metadata.partition() +
                            ", Offset: " + metadata.offset());
                } else {
                    System.err.println("Failed to send message: " + exception.getMessage());
                }
            });
            System.out.println("Sent message: " + jsonMessage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}