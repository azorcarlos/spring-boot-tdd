package br.com.azor.library.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.azor.library.api.model.entity.Loan;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

	private final LoanService loanService;
	
	private final EmailService emailService;
	
	@Value("${application.mail.message}")
	private String messagem;

	@Scheduled(cron = CRON_LATE_LOANS)
	public void sendMailToLateLoans() {

		List<Loan> allLateLoans = loanService.getAllLateLoans();

		List<String> listLoans = allLateLoans.stream().map(loan -> loan.getCustomerEmail())
				.collect(Collectors.toList());

		emailService.sendEmails(listLoans, messagem);

	}

}
