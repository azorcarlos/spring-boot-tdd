package br.com.library.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.library.api.model.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	boolean existsByIsbn(String isbn);

	Optional<Book> findByIsbn(String isbn);

}
