package br.com.azor.library.api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootTddApplication {

	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();

	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootTddApplication.class, args);
	}

}
