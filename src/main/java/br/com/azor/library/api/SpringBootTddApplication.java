package br.com.azor.library.api;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import br.com.azor.library.api.service.EmailService;

@SpringBootApplication
@EnableScheduling
public class SpringBootTddApplication {

	@Autowired
	private EmailService emailService;
	
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();

	}
	
	
	@Bean
	public CommandLineRunner runner() {
		return args ->{
			List<String> emails = Arrays.asList("libray-api-a4b730@inbox.mailtrap.io");
			emailService.sendEmails(emails, "enviando teste");
			System.out.println("Email enviado com Sucesso!");
		};
	}
	
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootTddApplication.class, args);
	}

}
