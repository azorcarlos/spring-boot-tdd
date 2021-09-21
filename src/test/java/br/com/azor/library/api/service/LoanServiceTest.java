package br.com.azor.library.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.azor.library.api.dto.LoanFilterDTO;
import br.com.azor.library.api.exception.BusinessException;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.model.entity.Loan;
import br.com.azor.library.api.payloads.BookPayloadFactor;
import br.com.azor.library.api.payloads.LoanPayloadFactory;
import br.com.azor.library.api.repository.LoanRepository;
import jdk.jfr.Description;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
@DisplayName("Service - Loan")
public class LoanServiceTest {

	@MockBean
	private LoanRepository repository;
	
	private LoanService service;
	
	
	@BeforeEach
	public void setUp() {
		this.service = new LoanServiceImpl(repository);
	}
	
	@Test
	@Description("Deve salvar um emprestimo")
	public void saveNewLoan() {

		Book newBook = BookPayloadFactor.bookDataBook();

		Loan loanService = Loan.builder()
				.customer("Jose")
				.loanDate(LocalDate.now())
				.book(newBook)
				.build();
		
		
		Loan loanRepository = Loan.builder()
				.id(1L)
				.customer("Jose")
				.loanDate(LocalDate.now())
				.book(newBook)
				.build();
		when(repository.existsByBookAndNotReturned(newBook)).thenReturn(false);
		when(repository.save(loanService)).thenReturn(loanRepository);
		
		Loan serviceReturn = service.save(loanService);
		
		verify(repository, times(1)).save(loanService);
		
		assertThat(serviceReturn.getId()).isEqualTo(loanRepository.getId());
		
		assertThat(serviceReturn.getCustomer())
			.isEqualTo(loanRepository.getCustomer());
		
		assertThat(serviceReturn.getLoanDate())
			.isEqualTo(loanRepository.getLoanDate());
		
		assertThat(serviceReturn.getBook())
			.isEqualTo(loanRepository.getBook());
		

	}
	
	
	@Test
	@Description("Deve retornar uma exceção ao tentar salvar um livro já emprestado")
	public void saveLoanBootEmprested() {

		// Dominio service
		Book book = BookPayloadFactor.bookDataBook();

		Loan loanService = Loan.builder()
				.customer("Jose")
				.loanDate(LocalDate.now())
				.book(book)
				.build();
		
		
		when(repository.existsByBookAndNotReturned(book)).thenReturn(true);
		
		Throwable exception = catchThrowable(()-> service.save(loanService));
		
		
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("Book already loaned");
		
		verify(repository, never()).save(loanService);
		

	}

	@Test
	@DisplayName("Deve obter as informações de um emprestimo pelo id")
	public void getLoandDetailTest() throws Exception {

		var id = 1L;
		var loan = LoanPayloadFactory.getDataLoan();

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

		var retorno = service.getById(id);

		assertThat(retorno.isPresent()).isTrue();

		assertThat(retorno.get().getId()).isEqualTo(id);

		assertThat(retorno.get().getCustomer()).isEqualTo(loan.getCustomer());

		assertThat(retorno.get().getLoanDate()).isEqualTo(loan.getLoanDate());

		assertThat(retorno.get().getBook()).isEqualTo(loan.getBook());

		verify(repository).findById(id);

	}
	
	@Test
	@DisplayName("Deve atualizar um emprestimo")
	public void updateLoan() throws Exception {

		Loan loan = LoanPayloadFactory.getDataLoan();
		loan.setReturned(true);

		Mockito.when(repository.save(loan)).thenReturn(loan);

		Loan updated = service.update(loan);

		assertThat(updated.getReturned()).isTrue();
		verify(repository).save(loan);

	}
	
	@Test
	@DisplayName("Deve filtrar emprestimos")
	public void findLoansTest() {
		Loan loan = LoanPayloadFactory.getDataLoan();
		LoanFilterDTO loanFilterDto = LoanFilterDTO.builder().isbn(loan.getBook().getIsbn())
				.customer(loan.getCustomer()).build();

		PageRequest pageRequest = PageRequest.of(0, 10);
		List<Loan> lista = Arrays.asList(loan);

		Page<Loan> page = new PageImpl<Loan>(lista, PageRequest.of(0, 10), 1);

		Mockito.when(repository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(),
				Mockito.any(PageRequest.class))).thenReturn(page);
		Page<Loan> result = service.find(loanFilterDto, pageRequest);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).isEqualTo(lista);
		assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(result.getPageable().getPageSize()).isEqualTo(10);

	}

}
