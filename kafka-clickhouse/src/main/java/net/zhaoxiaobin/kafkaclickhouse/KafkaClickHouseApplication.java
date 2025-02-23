package net.zhaoxiaobin.kafkaclickhouse;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Map;

@SpringBootApplication
public class KafkaClickHouseApplication {
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(KafkaClickHouseApplication.class, args);
    }

    @PostConstruct
    public void printDataSourceInfo() {
        // 验证druid配置是否生效
        if (dataSource instanceof DynamicRoutingDataSource) {
            DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
            Map<String, DataSource> dataSources = dynamicRoutingDataSource.getDataSources();
            dataSources.forEach((key, dataSource) -> {
                if (dataSource instanceof ItemDataSource) {
                    ItemDataSource itemDataSource = (ItemDataSource) dataSource;
                    DataSource realDataSource = itemDataSource.getDataSource();
                    if (realDataSource instanceof DruidDataSource) {
                        DruidDataSource druidDataSource = (DruidDataSource) realDataSource;
                        System.out.println(key + "-Initial size: " + druidDataSource.getInitialSize());
                        System.out.println(key + "-Max active: " + druidDataSource.getMaxActive());
                        System.out.println(key + "-Validation query: " + druidDataSource.getValidationQuery());
                        System.out.println(key + "-Test while idle: " + druidDataSource.isTestWhileIdle());
                    }
                }
            });
        }
    }
}
