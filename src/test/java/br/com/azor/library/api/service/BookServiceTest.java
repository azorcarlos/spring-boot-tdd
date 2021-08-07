package br.com.azor.library.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import br.com.azor.library.api.exception.BusinessException;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DisplayName("Services - BookService")
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
	@Order(value = 1)
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
	@Order(value = 2)
	public void shouldNotSaveABookWithDuplicatedISBNTest() {

		// Cenário
		Book book = createdValidBook();
		// Emular cenário
		// TODO : Estudar este ponto
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));

		// Verificação
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrado");

		Mockito.verify(repository, Mockito.never()).save(book);

	}
	
	@Test
	@DisplayName("Deve retornar um Livro")
	@Order(value = 3)
	public void ShouldReturnABookTest() {

		Long id = 11L;
		Book book = createdValidBook();
		book.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

		// Excutar
		Optional<Book> foundBook = service.findById(id);

		// Validar
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());

	}
	
	@Test
	@DisplayName("Deve retornar vazio quando consultar um livro inexistente")
	@Order(value = 4)
	public void ShouldNullABookTest() {

		Long id = 11L;
		

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Excutar
		Optional<Book> foundBook = service.findById(id);

		// Validar
		assertThat(foundBook.isEmpty()).isTrue();

	}
	
	@Test
	@DisplayName("Deve exclir um livro")
	public void ShouldDeleteBookTest() {
		Book book = Book.builder().id(11L).build();
	
		assertDoesNotThrow(()->service.deleteById(book.getId()));
		Mockito.verify(repository, Mockito.times(1)).deleteById(book.getId());

	}
	
	@Test
	@DisplayName("Deve ocorrer um erro ao deletar um livro inexisten")
	public void DeleteBookInvalidTest() {
		Book book = new Book();
		assertThrows(IllegalArgumentException.class, () ->service.deleteById(book.getId()));
		
		Mockito.verify(repository, Mockito.never()).delete(book);
		
	
	}
	
	@Test
	@DisplayName("Deve ocorrer um erro tentar atualizar um livro inexisten")
	public void UpdatedBookInvalidTest() {
		Book book = new Book();
		assertThrows(IllegalArgumentException.class, () ->service.update(book));
		
		Mockito.verify(repository, Mockito.never()).delete(book);
		
	
	}
	
	
	@Test
	@DisplayName("Deve atualizar um livro valido")
	public void UpdateBookTest() {
		Long id = 11L;

		Book book = createdValidBook();
		book.setId(id);

		Mockito.when(repository.save(book)).thenReturn(book);

		Book bookReturn = service.update(book);

		assertThat(bookReturn.getId()).isEqualTo(book.getId());
		assertThat(bookReturn.getAuthor()).isEqualTo(book.getAuthor());
		assertThat(bookReturn.getIsbn()).isEqualTo(book.getIsbn());
		assertThat(bookReturn.getTitle()).isEqualTo(book.getTitle());

	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Deve filtrar livros pelas propriedades")
	public void findBookTest() {
		Book book = createdValidBook();

		PageRequest pageRequest = PageRequest.of(0, 10);

		List<Book> lista = Arrays.asList(book);
		Page<Book> page = new PageImpl<Book>(lista, PageRequest.of(0, 10), 1);

		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

		Page<Book> result = service.find(book, pageRequest);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(lista);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);

	}

	
	
	private Book createdValidBook() {
		Book book = Book.builder().author("Azor").title("O mundo é legal").isbn("1212121").build();
		return book;
	}

}
















