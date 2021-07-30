package br.com.azor.library.api.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	BookRepository repository;

	@Test
	@DisplayName("Deve retornar verdadeiro quando exitir um libro na base com o isbn informado")
	public void returnsTrueIfIsbnExists() {

		// cenário
		Book book = Book.builder().title("Meu Livros").author("Azor").isbn("121212").build();
		entityManager.persist(book);

		String isbn = "121212";
		// executção

		boolean exits = repository.existsByIsbn(isbn);
		// validação

		assertThat(exits).isTrue();

	}

	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
	public void returnsFalseIfIsbnExists() {

		// cenário

		String isbn = "121212";
		// executção

		boolean exits = repository.existsByIsbn(isbn);
		// validação

		assertThat(exits).isFalse();

	}

}
