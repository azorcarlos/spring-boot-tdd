package br.com.azor.library.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.azor.library.api.dto.BookDTO;
import br.com.azor.library.api.dto.LoanDTO;
import br.com.azor.library.api.dto.LoanFilterDTO;
import br.com.azor.library.api.dto.ReturnedLoanDTO;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.model.entity.Loan;
import br.com.azor.library.api.service.BookService;
import br.com.azor.library.api.service.LoanService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

	private final BookService bookService;
	private final LoanService service;
	private final ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public Long create(@RequestBody LoanDTO dto) {
		Book book = bookService.getBookByIsbn(dto.getIsbn())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found passed isbn"));

		Loan entity = Loan.builder().book(book).loanDate(null).returned(false).build();
		entity = service.save(entity);
		return entity.getId();

	}
	
	@PatchMapping("/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public void returnBook(@PathVariable("id") Long id, @RequestBody ReturnedLoanDTO dto) {

		Loan loan = service.getById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		loan.setReturned(dto.getReturned());

		service.update(loan);
	}
	
	@GetMapping
	public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
		Page<Loan> result = service.find(dto, pageRequest);

		System.out.println(dto);
		List<LoanDTO> list = result.getContent()
				.stream()
				.map(entity -> {
					Book book = entity.getBook();
					
					BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
					
					LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
					
					loanDTO.setBook(bookDTO);
					
					return loanDTO;
				})
				.collect(Collectors.toList());
		return new PageImpl<LoanDTO>(list, pageRequest, result.getTotalElements());

	}

}
