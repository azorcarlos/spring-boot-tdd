package br.com.azor.library.api.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.azor.library.api.dto.LoanFilterDTO;
import br.com.azor.library.api.exception.BusinessException;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.model.entity.Loan;
import br.com.azor.library.api.repository.LoanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

	private final LoanRepository repository;

	@Override
	public Loan save(Loan loan) {
		if (repository.existsByBookAndNotReturned(loan.getBook())) {
			throw new BusinessException("Book already loaned");
		}
		return repository.save(loan);
	}

	@Override
	public Optional<Loan> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public Loan update(Loan loan) {
		return repository.save(loan);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO filter, Pageable pageable) {
		return repository.findByBookIsbnOrCustomer(filter.getIsbn(), filter.getCustomer(), pageable);
	}

	@Override
	public Page<Loan> getLoansByBook(Book book, Pageable pegeable) {
		return repository.findByBook(book, pegeable);
	}

}
