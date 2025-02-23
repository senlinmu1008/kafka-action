package net.zhaoxiaobin.kafkaclickhouse.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhaoxb
 * @date 2025-02-23 19:42
 */
@Data
public class IndicatorDataDTO {
    /**
     * 指标编码
     */
    private String indicatorCode;

    /**
     * 指标值，BigDecimal(20,4)
     */
    private BigDecimal indicatorValue;

    /**
     * 指标时间，yyyyMMddHHmmss
     */
    private String time;

    /**
     * 修改时间，yyyy-MM-dd HH:mm:ss
     */
    private String updateTime;

    /**
     * 资产标识
     * assetId有为空的情况，assetId不为空时，说明此条指标数据是设备指标，为空时，说明此条数据是组织指标（如分厂指标，基地指标等），丢弃处理
     */
    private String assetId;

    /**
     * 时间维度标识（Y:年，M:月，D:日，H:小时）
     */
    private String periodicity;
}