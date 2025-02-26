package net.zhaoxiaobin.kafkaclickhouse.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.zhaoxiaobin.kafkaclickhouse.config.DBConfigConstant;
import net.zhaoxiaobin.kafkaclickhouse.entity.dto.IndicatorDataDTO;
import net.zhaoxiaobin.kafkaclickhouse.entity.po.MetricDataPO;
import net.zhaoxiaobin.kafkaclickhouse.mapper.MetricDataMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通过kafka同步指标数据service
 *
 * @author zhaoxb
 * @date 2025-02-20 15:27:36
 */
@Slf4j
@Service
public class IndicatorDataSyncKafkaService extends ServiceImpl<MetricDataMapper, MetricDataPO> {

    @KafkaListener(topics = "indicatorDataSync", groupId = "indicatorDataSyncGroup")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("芜空压指标数据同步开始处理");
        try {
            // 判断资产标识，如果为空，丢弃处理
            List<MetricDataPO> metricDataPOList = JSON.parseArray(record.value(), IndicatorDataDTO.class)
                    .stream().filter(indicatorDataDTO -> StringUtils.isNotBlank(indicatorDataDTO.getAssetId()))
                    .map(indicatorDataDTO -> {
                        // 数据简单转换
                        MetricDataPO metricDataPO = new MetricDataPO();
                        metricDataPO.setMetricSourceCode(indicatorDataDTO.getAssetId());
                        metricDataPO.setMetricCode(indicatorDataDTO.getIndicatorCode());
                        metricDataPO.setMetricValue(indicatorDataDTO.getIndicatorValue());
                        metricDataPO.setMetricTime(DateUtil.parseLocalDateTime(indicatorDataDTO.getTime(), "yyyyMMddHHmmss"));
                        metricDataPO.setMetricType(0); // 这里固定设备类型
                        metricDataPO.setModifyTime(DateUtil.parseLocalDateTime(indicatorDataDTO.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
                        metricDataPO.setPeriodicity(indicatorDataDTO.getPeriodicity());
                        return metricDataPO;
                    }).collect(Collectors.toList());
            // 因为这里不是直接调用mapper方法进行批量插入，所以需要手动切换数据源
            DynamicDataSourceContextHolder.push(DBConfigConstant.CLICKHOUSE);
            this.saveBatch(metricDataPOList);
            ack.acknowledge();
            log.info("芜空压指标数据同步处理完成，本次共有数据:{}条", metricDataPOList.size());
        } catch (Exception e) {
            log.error("芜空压指标数据同步处理失败", e);
        } finally {
            DynamicDataSourceContextHolder.clear();
        }
    }
}