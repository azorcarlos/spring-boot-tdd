package br.com.azor.library.api.payloads;

import java.time.LocalDate;

import br.com.azor.library.api.model.entity.Loan;

public class LoanPayloadFactory {
	
	public static Loan getNewLoan() {
		return Loan.builder()
				.customer("Fulano")
				.book(BookPayloadFactor.bookDataBook())
				.build();
	}
	
	
	public static Loan getDataLoan() {
		return Loan.builder()
				.id(1L)
				.loanDate(LocalDate.now())
				.returned(false)
				.customer("Fulano")
				.book(BookPayloadFactor.bookDataBook())
				.build();
	}

}
