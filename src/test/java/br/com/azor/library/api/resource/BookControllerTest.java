package br.com.azor.library.api.resource;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.azor.library.api.dto.BookDTO;
import br.com.azor.library.api.exception.BusinessException;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	// Mocar as requisições
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService service;	

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
	public void createBookWithDuplicatedIsbn() throws Exception {
		
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
	

	//Utilitários
	private BookDTO createNewBook() {
		
		BookDTO bookRequest = BookDTO.builder()
				.title("Meu Livros")
				.author("Azor")
				.isbn("121212")
				.build();
		return bookRequest;
	}
	

}
