package net.zhaoxiaobin.kafkaclickhouse.entity.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhaoxb
 * @date 2025-02-23 19:43
 */
@Data
@TableName("metric_data")
public class MetricDataPO {
    /**
     * 指标来源编码
     */
    @TableField("metric_source_code")
    private String metricSourceCode;

    /**
     * 指标编码
     */
    @TableField("metric_code")
    private String metricCode;

    /**
     * 指标值
     */
    @TableField("metric_value")
    private BigDecimal metricValue;

    /**
     * 指标时间
     */
    @TableField("metric_time")
    private LocalDateTime metricTime;

    /**
     * 指标类型 0:设备 1:组织 2:产品
     */
    @TableField("metric_type")
    private Integer metricType;

    /**
     * 修改时间
     */
    @TableField("modify_time")
    private LocalDateTime modifyTime;

    /**
     * 维度 H/D/M/Y
     */
    @TableField("periodicity")
    private String periodicity;
}