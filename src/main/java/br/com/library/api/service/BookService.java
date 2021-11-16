package br.com.library.api.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.library.api.model.entity.Book;

public interface BookService {

	Book save(Book book);

	Optional<Book> findById(Long id);

	void deleteById(Long id);

	Book update(Book book);

	Page<Book> find(Book filter, Pageable pageable);

	Optional<Book> getBookByIsbn(String isbn);

}
