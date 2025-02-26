package net.zhaoxiaobin.kafkaclickhouse.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.zhaoxiaobin.kafkaclickhouse.config.DBConfigConstant;
import net.zhaoxiaobin.kafkaclickhouse.entity.po.MetricDataPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author zhaoxb
 * @date 2025-02-23 19:45
 */
@Mapper
@DS(DBConfigConstant.CLICKHOUSE)
public interface MetricDataMapper extends BaseMapper<MetricDataPO> {

}