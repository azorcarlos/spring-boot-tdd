package br.com.azor.library.api;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class SpringBootTddApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();

	}
	
	

	@Scheduled(cron = "0 31 12 1/1 * ?")
	public void testeAgendamentoTarefas() {
		
		System.out.println("Ola teste");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootTddApplication.class, args);
	}

}
