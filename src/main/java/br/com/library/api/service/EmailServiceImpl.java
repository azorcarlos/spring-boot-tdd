package br.com.library.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

	private final JavaMailSender mailSender;

	@Value("${appliction.email.remetente}")
	private String remetendEmail;

	@Override
	public void sendEmails(List<String> listLoans, String messagem) {

		String[] listEmalis = listLoans.toArray(new String[listLoans.size()]);
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(remetendEmail);
		mailMessage.setSubject("Livro emprestimo atrassado");
		mailMessage.setText(messagem);
		
		mailMessage.setTo(listEmalis);

		mailSender.send(mailMessage);

	}

}
