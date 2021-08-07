package br.com.azor.library.api.service;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.azor.library.api.exception.BusinessException;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;
	
	public BookServiceImpl(BookRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("Isbn já cadastrado");
			
		}

		return repository.save(book);
	}

	@Override
	public Optional<Book> findById(Long id) {
		return this.repository.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		if(id == null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		this.repository.deleteById(id);
		
	}

	@Override
	public Book update(Book book) {
		
		if(book == null || book.getId() ==null) {
			throw new IllegalArgumentException("Book id cant be null");
		}
		return this.repository.save(book);

	}

	@Override
	public Page<Book> find(Book filter, Pageable pageable) {
		Example<Book> example =  Example.of(filter, 
				ExampleMatcher
					.matching()
					.withIgnoreCase()
					.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
				);
		return this.repository.findAll(example, pageable);
	}

}
