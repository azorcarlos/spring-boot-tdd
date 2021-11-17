package br.com.library.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.library.api.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("teste")
@DataJpaTest
@DisplayName("Repository - Book")
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	BookRepository repository;

	@Test
	@DisplayName("Deve retornar verdadeiro quando exitir um libro na base com o isbn informado")
	public void returnsTrueIfIsbnExistsTest() {

		// cenário
		Book book = createNewBook();
		entityManager.persist(book);

		String isbn = "121212";
		// executção

		boolean exits = repository.existsByIsbn(isbn);
		// validação

		assertThat(exits).isTrue();

	}

	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
	public void returnsFalseIfIsbnExistsTest() {

		// cenário

		String isbn = "121212";
		// executção

		boolean exits = repository.existsByIsbn(isbn);
		// validação

		assertThat(exits).isFalse();

	}

	@Test
	@DisplayName("Deve retornar um livro por id")
	public void MustReturnAbookIdTest() {

		Book book = createNewBook();
		entityManager.persist(book);

		Optional<Book> foundBook = repository.findById(book.getId());

		assertThat(foundBook.isPresent()).isTrue();

	}

	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		Book book = createNewBook();
		Book saveBook = repository.save(book);

		assertThat(saveBook.getId()).isNotNull();

	}

	@Test
	@DisplayName("Deve Deletar um livro")
	public void deleteToBookTest() {

		Book book = createNewBook();
		entityManager.persist(book);
		Book foundBook = entityManager.find(Book.class, book.getId());

		repository.delete(foundBook);

		Book deleteBook = entityManager.find(Book.class, book.getId());
		assertThat(deleteBook).isNull();

	}
	 
	private Book createNewBook() {
		Book book = Book.builder().title("Meu Livros").author("Azor").isbn("121212").build();
		return book;
	}
}
