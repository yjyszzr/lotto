package com.dl.shop.lotto;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.dl.base.configurer.FeignConfiguration;
import com.dl.base.configurer.RestTemplateConfig;
import com.dl.base.configurer.WebMvcConfigurer;
import com.dl.shop.lotto.configurer.Swagger2;
import com.dl.shop.lotto.core.ProjectConstant;

@SpringBootApplication
@Import({RestTemplateConfig.class, Swagger2.class, WebMvcConfigurer.class, FeignConfiguration.class})
@MapperScan(basePackages= {ProjectConstant.MAPPER_PACKAGE,"com.dl.shop.lotto.dao"})
@EnableTransactionManagement
@EnableEurekaClient
@EnableFeignClients({"com.dl.member.api", "com.dl.order.api", "com.dl.shop.payment.api"})
public class LottoServiceApplication {

	public static void main(String[] args) {
        SpringApplication.run(LottoServiceApplication.class, args);
    }

}
