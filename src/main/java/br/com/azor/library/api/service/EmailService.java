package br.com.azor.library.api.service;

import java.util.List;

public interface EmailService {

	public void sendEmails(List<String> listLoans, String messagem);
}
