package br.com.library.api.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.library.api.dto.LoanDTO;
import br.com.library.api.dto.LoanFilterDTO;
import br.com.library.api.dto.ReturnedLoanDTO;
import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.entity.Loan;
import br.com.library.api.payloads.BookPayloadFactor;
import br.com.library.api.payloads.LoanPayloadFactory;
import br.com.library.api.resource.LoanController;
import br.com.library.api.service.BookService;
import br.com.library.api.service.LoanService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
@DisplayName("Controller - LoanController")
public class LoanControllerTest {

	static final String LOAN_API = "/api/loans";

	@Autowired
	MockMvc mvc;
	
	@MockBean
	private BookService booKservice;

	@MockBean
	private LoanService loanService;

	@Test
	@DisplayName("Deve realizar um emprestimo")
	public void createdNewLoanTest() throws Exception {

		LoanDTO dto = LoanDTO.builder().customer("Fulano").email("fulano@email.com").isbn("123").build();
		String json = new ObjectMapper().writeValueAsString(dto);

		BDDMockito.given(booKservice.getBookByIsbn(dto.getIsbn())).willReturn(Optional.of(BookPayloadFactor.bookDataBook()));

		Loan loan = LoanPayloadFactory.getDataLoan();

		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);

		mvc.perform(request)
			.andExpect(status().isCreated())
			.andExpect(content().string(loan.getId().toString()));
			
		

	}


	@Test
	@DisplayName("Deve retornar erro para livro inexistemte")
	public void invalidIsbnCreateLoanTest() throws Exception {
		
		LoanDTO dto = LoanDTO.builder().customer("Fulano").isbn("123").build();
		String json = new ObjectMapper().writeValueAsString(dto);

		BDDMockito.given(booKservice.getBookByIsbn(dto.getIsbn())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);

		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Book not found passed isbn"));
			
		
	}
	
	
	@Test
	@DisplayName("Deve retornar erro para livro já emprestado")
	public void loaneBookErrorsOnCreateLoanTest() throws Exception {
		
		LoanDTO dto = LoanDTO.builder().customer("Fulano").isbn("123").build();
		String json = new ObjectMapper().writeValueAsString(dto);

		BDDMockito.given(booKservice.getBookByIsbn(dto.getIsbn())).willReturn(Optional.of(BookPayloadFactor.bookDataBook()));
		
		BDDMockito.given(loanService.save(Mockito.any(Loan.class)) )
			.willThrow(new BusinessException("Book already loaned"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json);

		mvc.perform(request)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("errors", Matchers.hasSize(1)))
			.andExpect(jsonPath("errors[0]").value("Book already loaned"));
			
		
	}
	
	@Test
	@DisplayName("Deve retornar um emprestimo")
	public void returnBookTest() throws Exception {

		ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(LoanPayloadFactory.getDataLoan()));

		mvc.perform(patch(LOAN_API.concat("/1"))
					.accept(MediaType.APPLICATION_JSON)
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(json)
				    ).andExpect(status().isOk());

	}
	
	@Test
	@DisplayName("Deve retornar Not Found ao atualizar emprestimo inexistente")
	public void returnBookNotFoundTest() throws Exception {

		ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(patch(LOAN_API.concat("/1"))
					.accept(MediaType.APPLICATION_JSON)
				    .contentType(MediaType.APPLICATION_JSON)
				    .content(json)
				    ).andExpect(status().isNotFound());

	}
	
	@Test
	@DisplayName("Deve filtrar emprestimos")
	public void findLoanTest () throws Exception {
		
		Loan loan = LoanPayloadFactory.getDataLoan();
			
		BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
			.willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10),1));
		
		String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10", loan.getBook().getIsbn(), loan.getCustomer());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(LOAN_API.concat(queryString))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("content", Matchers.hasSize(1)))
			.andExpect(jsonPath("totalElements").value(1))
			.andExpect(jsonPath("pageable.pageSize").value(10))
			.andExpect(jsonPath("pageable.pageNumber").value(0));
		
	}
	
	
}
