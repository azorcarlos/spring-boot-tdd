package br.com.azor.library.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.azor.library.api.exception.BusinessException;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {
	
	BookService service;
	
	@MockBean
	BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		//TODO : Estudar este ponto? Construtor ao invés de Autoried
		this.service = new BookServiceImpl(repository);
	}
	
	
	
	@Test
	@DisplayName("Deve criar um novo cadastro")
	public void saveBookTest() {

		// cenário
		Book book = createdValidBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		// Mocks do retorno do service
		//TODO : Estudar este ponto Mocar retorno de crud
		Mockito.when(repository.save(book))
				.thenReturn(Book.builder().id(11L).author("Azor").title("O mundo é legal").isbn("1212121").build());
		// execução
		Book savedBook = service.save(book);

		// Verificação
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getTitle()).isEqualTo("O mundo é legal");
		assertThat(savedBook.getIsbn()).isEqualTo("1212121");
		assertThat(savedBook.getAuthor()).isEqualTo("Azor");

	}


	
	
	@Test
	@DisplayName("Deve-se lançar uma exceção ao tentar cadastrar um libro com o ISBN duplicado")
	public void shouldNotSaveABookWithDuplicatedISBN() {

		// Cenário
		Book book = createdValidBook();
		// Emular cenário
		//TODO : Estudar este ponto
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));

		// Verificação
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado");

		Mockito.verify(repository, Mockito.never()).save(book);

	}
	
	
	private Book createdValidBook() {
		Book book = Book.builder().author("Azor").title("O mundo é legal").isbn("1212121").build();
		return book;
	}

}
















