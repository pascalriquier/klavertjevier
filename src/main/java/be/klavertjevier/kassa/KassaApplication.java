package be.klavertjevier.kassa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class KassaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KassaApplication.class, args);
	}

}
