package it.unipi.enPassant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(
				exclude = {
								DataSourceAutoConfiguration.class,
								HibernateJpaAutoConfiguration.class
				}
)

public class EnPassantApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnPassantApplication.class, args);
	}

}
