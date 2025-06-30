package server.yakssok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import server.yakssok.global.common.jwt.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class YakssokApplication {

	public static void main(String[] args) {
		SpringApplication.run(YakssokApplication.class, args);
	}

}
