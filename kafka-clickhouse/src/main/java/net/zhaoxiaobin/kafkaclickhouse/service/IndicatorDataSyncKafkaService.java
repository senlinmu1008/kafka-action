package net.zhaoxiaobin.kafkaclickhouse.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import net.zhaoxiaobin.kafkaclickhouse.entity.dto.IndicatorDataDTO;
import net.zhaoxiaobin.kafkaclickhouse.entity.po.MetricDataPO;
import net.zhaoxiaobin.kafkaclickhouse.mapper.MetricDataMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * 通过kafka同步指标数据service
 *
 * @author zhaoxb
 * @date 2025-02-20 15:27:36
 */
@Slf4j
@Service
public class IndicatorDataSyncKafkaService {
    @Autowired
    private MetricDataMapper metricDataMapper;

    @KafkaListener(topics = "indicatorDataSync", groupId = "indicatorDataSyncGroup")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("开始处理芜空压指标数据同步Kafka消息: {}", record.value());
        try {
            IndicatorDataDTO indicatorDataDTO = JSON.parseObject(record.value(), IndicatorDataDTO.class);
            // 判断资产标识，如果为空，丢弃处理
            String assetId = indicatorDataDTO.getAssetId();
            if (StringUtils.isBlank(assetId)) {
                log.warn("资产标识为空，丢弃处理: {}", record.value());
                ack.acknowledge();
                return;
            }
            // 数据简单转换后，存入ClickHouse
            MetricDataPO metricDataPO = new MetricDataPO();
            metricDataPO.setMetricSourceCode(assetId);
            metricDataPO.setMetricCode(indicatorDataDTO.getIndicatorCode());
            metricDataPO.setMetricValue(indicatorDataDTO.getIndicatorValue());
            metricDataPO.setMetricTime(DateUtil.parseLocalDateTime(indicatorDataDTO.getTime(), "yyyyMMddHHmmss"));
            metricDataPO.setMetricType(0); // 这里固定设备类型
            metricDataPO.setModifyTime(DateUtil.parseLocalDateTime(indicatorDataDTO.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            metricDataPO.setPeriodicity(indicatorDataDTO.getPeriodicity());
            metricDataMapper.insert(metricDataPO);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("芜空压指标数据同步，Kafka消息处理失败: {}", record.value(), e);
        }
    }
}