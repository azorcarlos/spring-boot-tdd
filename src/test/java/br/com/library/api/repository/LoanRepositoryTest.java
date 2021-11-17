package br.com.library.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.library.api.model.entity.Book;
import br.com.library.api.model.entity.Loan;
import br.com.library.api.payloads.BookPayloadFactor;
import br.com.library.api.payloads.LoanPayloadFactory;
import jdk.jfr.Description;

@ExtendWith(SpringExtension.class)
@ActiveProfiles(value = "test")
@DataJpaTest
@DisplayName("Repository - Loan")
public class LoanRepositoryTest {

	@Autowired
	private LoanRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@Description("Deve verificar se existe um emprestimo não devolvido para o Livro")
	public void saveLoan() {

		Book book = BookPayloadFactor.createNewBook();
		entityManager.persist(book);

		Loan loan = LoanPayloadFactory.getNewLoan(LocalDate.now());
		loan.setBook(book);

		entityManager.persist(loan);

		boolean exists = repository.existsByBookAndNotReturned(book);

		assertThat(exists).isTrue();

	}

	@Test
	@DisplayName("Deve obter empresimo vencidos a três dias atrás")
	public void findByLoanDateLessThanAndNotReturnedTest() {

		Book book = BookPayloadFactor.createNewBook();
		entityManager.persist(book);

		Loan loan = LoanPayloadFactory.getNewLoan(LocalDate.now().minusDays(5));
		loan.setBook(book);

		entityManager.persist(loan);

		var result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

		assertThat(result).hasSize(1).contains(loan);

	}
	
	@Test
	@DisplayName("Não deve retornar empresimo vencidos a três dias atrás")
	public void NotfindByLoanDateLessThanAndNotReturnedTest() {

		Book book = BookPayloadFactor.createNewBook();
		entityManager.persist(book);

		Loan loan = LoanPayloadFactory.getNewLoan(LocalDate.now());
		loan.setBook(book);

		entityManager.persist(loan);

		var result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

		assertThat(result).isEmpty();

	}

	@Test
	@DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
	public void findByBookIsbnOrCustomerTest() {

		Book book = BookPayloadFactor.createNewBook();
		entityManager.persist(book);

		Loan loan = LoanPayloadFactory.getNewLoan(LocalDate.now());
		loan.setBook(book);

		entityManager.persist(loan);

		var retorno = repository.findByBookIsbnOrCustomer(book.getIsbn(), loan.getCustomer(), PageRequest.of(0, 10));

		assertThat(retorno.getContent()).hasSize(1);
		assertThat(retorno.getContent()).contains(loan);
		assertThat(retorno.getPageable().getPageSize()).isEqualTo(10);
		assertThat(retorno.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(retorno.getTotalElements()).isEqualTo(1);

	}
}
