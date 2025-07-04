package server.yakssok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan("server.yakssok")
public class YakssokApplication {

	public static void main(String[] args) {
		SpringApplication.run(YakssokApplication.class, args);
	}

}
