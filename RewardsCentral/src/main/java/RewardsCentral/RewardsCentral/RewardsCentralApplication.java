package RewardsCentral.RewardsCentral;

import gpsUtil.GpsUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import rewardCentral.RewardCentral;

@SpringBootApplication
@EnableDiscoveryClient
public class RewardsCentralApplication {

  @Bean
  public GpsUtil gpsUtil() {
    return new GpsUtil();
  }

  @Bean
  public RewardCentral rewardCentral() {
    return new RewardCentral();
  }
	public static void main(String[] args) {
		SpringApplication.run(RewardsCentralApplication.class, args);
	}

}
