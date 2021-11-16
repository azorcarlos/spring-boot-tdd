package br.com.azor.library.api.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.azor.library.api.dto.LoanFilterDTO;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.model.entity.Loan;

public interface LoanService {

	Loan save(Loan loan);

	Optional<Loan> getById(Long id);

	Loan update(Loan loan);

	Page<Loan> find(LoanFilterDTO filter, Pageable pageable);

	Page<Loan> getLoansByBook(Book book, Pageable pegeable);

}
