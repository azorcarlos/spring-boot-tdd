package br.com.library.api.resource;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.entity.Book;
import br.com.library.api.service.BookService;
import br.com.library.api.service.LoanService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
@DisplayName("Controller - BookController")
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	// Mocar as requisições
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService service;	
	
	@MockBean
	private LoanService loanService;

	@Test
	@DisplayName("Criar Cadastro de Books com Sucesso")
	public void createBookTest() throws Exception {
		BookDTO bookRequest = createNewBook();
		
		Book newBook = Book.builder()
				.id(101L)
				.title("Meu Livros")
				.author("Azor")
				.isbn("121212")
				
				.build();

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(newBook);
		
		String json = new ObjectMapper().writeValueAsString(bookRequest);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(101))
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(bookRequest.getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(bookRequest.getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookRequest.getIsbn()));

	}

	@Test
	@DisplayName("Deve-se Lançar um erro quando ouver irros de validações no payload")
	public void createInvalidBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(new BookDTO());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		

		mvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("errors", hasSize(3)));


	 }
	
	@Test
	@DisplayName("Deve lançar uma exeção ao tentar cadastrar um livro com ispn existente")
	public void createBookWithDuplicatedIsbnTest() throws Exception {
		
		//Regra de negócio
		BookDTO bookRequest = createNewBook();
		String json = new ObjectMapper().writeValueAsString(bookRequest);
		String messageError = "Isbn já cadastrado";
		
		//Manipular comportamento Mock
		BDDMockito.given(service.save(Mockito.any(Book.class)))
			.willThrow(new BusinessException(messageError));
	
	
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("errors", hasSize(1)))
		.andExpect(jsonPath("errors[0]").value(messageError));
		
		
	}
	
	@Test
	@DisplayName("Deve obter informações de um livro")
	public void getBookListTest() throws Exception {

		// Cenário
		Long id = 11L;

		Book book = Book.builder().id(id).title(createNewBook().getTitle()).isbn(createNewBook().getIsbn())
				.author(createNewBook().getAuthor()).build();

		BDDMockito.given(service.findById(id)).willReturn(Optional.of(book));

		// Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);
		mvc.perform(request).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
				.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
				.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));

	}
	
	@Test
	@DisplayName("Deve retornar um Not Found")
	public void bookNotFoundTest() throws Exception {

		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

		// Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/1"))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNotFound());

	}
	
	@Test
	@DisplayName("Deve deletar um registro existente")
	public void mustDeleteBookTest() throws Exception {
		
		Long id = 11L;
		Book book = Book.builder().id(id).build();
		
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(book));

		// Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+id))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNoContent());
		
		
		
		
	}
	
	
	@Test
	@DisplayName("Deve retornar Not Found quando não encontrar o livro para excluir")
	public void deleteInexistentBookTest() throws Exception {

		Long id = 11L;

		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

		// Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNotFound());

	}
	
	@Test
	@DisplayName("Atualizar dados de um Livro existente")
	public void updateBookTest() throws Exception {
		
		// Cenário
				
		 Long id = 11L;
		 String json = new ObjectMapper().writeValueAsString(createNewBook());
		
		 Book updatingBook = Book.builder().id(id).title(createNewBook().getTitle()).isbn(createNewBook().getIsbn())
				.author(createNewBook().getAuthor()).build();
		
		 
		 //GOTO Estudar
		 //Morcar findById
		 BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.of(updatingBook));
		 //Mocar services
		 BDDMockito.given(service.update(updatingBook)).willReturn(updatingBook);
		 //Execução
		 MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				  .put(BOOK_API.concat("/"+id))
				  .content(json)
				  .accept(MediaType.APPLICATION_JSON)
				  .contentType(MediaType.APPLICATION_JSON);
		 mvc.perform(request)
		 	.andExpect(status().isOk())
		 	.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
		 	.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
			.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
			.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
		
				
		
	}
	
	
	@Test
	@DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
	public void returnNotFoundBookInexistentTest() throws Exception {
		
		 Long id = 11L;
		 String json = new ObjectMapper().writeValueAsString(createNewBook());
		 BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());
		 
		 //Execução
		 MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				  .put(BOOK_API.concat("/"+id))
				  .content(json)
				  .accept(MediaType.APPLICATION_JSON)
				  .contentType(MediaType.APPLICATION_JSON);
		 
		 mvc.perform(request)
		 	.andExpect(status().isNotFound());
		
	}
	
	
	@Test
	@DisplayName("Deve Listar todos os livros")
	public void findBookAllTest () throws Exception {
		
		Long id = 11L;
		Book book = Book.builder()
					.id(id)
					.title(createNewBook().getTitle())
					.author(createNewBook().getAuthor())
					.isbn(createNewBook().getIsbn())
					.build();
			
		BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
			.willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100),1));
		
		String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("content", Matchers.hasSize(1)))
			.andExpect(jsonPath("totalElements").value(1))
			.andExpect(jsonPath("pageable.pageSize").value(100));
			//.andExpect(jsonPath("pageable.total").value(0));
		
	}
	


	private BookDTO createNewBook() {
		BookDTO bookRequest = BookDTO.builder().title("Meu Livros").author("Azor").isbn("121212").build();
		return bookRequest;
	}

}
