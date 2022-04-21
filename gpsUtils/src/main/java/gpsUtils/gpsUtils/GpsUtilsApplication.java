package gpsUtils.gpsUtils;

import gpsUtil.GpsUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class GpsUtilsApplication {


  @Bean
  public GpsUtil gpsUtil() {
    return new GpsUtil();
  }
	public static void main(String[] args) {
		SpringApplication.run(GpsUtilsApplication.class, args);
	}

}
