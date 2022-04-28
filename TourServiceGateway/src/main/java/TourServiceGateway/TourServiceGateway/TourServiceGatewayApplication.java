package TourServiceGateway.TourServiceGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableHystrix
public class TourServiceGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(TourServiceGatewayApplication.class, args);
	}

}
