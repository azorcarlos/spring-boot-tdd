package br.com.azor.library.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.model.entity.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

	
	@Query(value = "select case when (count(l.id) > 0) then true else false end "
			+ "from Loan l "
			+ " where l.book = :bookEntity "
			+ "and (l.returned is null or l.returned is false)")
	boolean existsByBookAndNotReturned(@Param("bookEntity") Book bookEntity);

	 @Query( value = " select l from Loan as l join l.book as b where b.isbn = :isbn or l.customer =:customer ")
	    Page<Loan> findByBookIsbnOrCustomer(
	            @Param("isbn") String isbn,
	            @Param("customer") String customer,
	            Pageable pageable
	    );

}