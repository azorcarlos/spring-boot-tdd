package br.com.library.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.dto.LoanDTO;
import br.com.library.api.model.entity.Book;
import br.com.library.api.model.entity.Loan;
import br.com.library.api.service.BookService;
import br.com.library.api.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
public class BookController {

	private final BookService service;
	private final ModelMapper modelMapper;
	private final LoanService loanService;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	@ApiOperation("Create a Book")
	public BookDTO create(@RequestBody @Valid BookDTO dto) {

		Book entity = modelMapper.map(dto, Book.class);

		entity = service.save(entity);

		return modelMapper.map(entity, BookDTO.class);

	}

	@GetMapping("{id}")
	@ApiOperation("Get book by id")
	public BookDTO get(@PathVariable Long id) {

		return service.findById(id).map(book -> modelMapper.map(service.findById(id).get(), BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	}

	@GetMapping
	@ApiOperation("List all books")
	public Page<BookDTO> findAll(BookDTO dto, Pageable pageRequest) {
		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);

		List<BookDTO> list = result.getContent().stream().map(entity -> modelMapper.map(entity, BookDTO.class))
				.collect(Collectors.toList());

		return new PageImpl<BookDTO>(list, (Pageable) pageRequest, result.getTotalElements());

	}

	@PutMapping("{id}")
	@ResponseStatus(code = HttpStatus.OK)
	@ApiOperation("Update books")
	public BookDTO update(@PathVariable Long id, @RequestBody BookDTO bookDTO) {

		Book book = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		book.setAuthor(bookDTO.getAuthor());
		book.setTitle(bookDTO.getTitle());
		service.update(book);

		return modelMapper.map(book, BookDTO.class);

	}

	@DeleteMapping("{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@ApiOperation("Excluir livro")
	@ApiResponses({
		@ApiResponse(code = 204, message = "Book successfully deleted!"),
		@ApiResponse(code = 401, message = "Unauthorized procedure"),
		@ApiResponse(code = 403, message = "Access denied!"),
	})
	public void deleteById(@PathVariable Long id) {

		Book book = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.deleteById(book.getId());

	}

	@GetMapping("{id}/loans")
	@ApiOperation("Return loans per book")
	public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pegeable) {
		Book book = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		Page<Loan> result = loanService.getLoansByBook(book, pegeable);

		var lista = result.getContent().stream().map(loan -> {

			var loanBook = loan.getBook();
			var bookDto = modelMapper.map(loanBook, BookDTO.class);
			var loanDto = modelMapper.map(loan, LoanDTO.class);

			loanDto.setBook(bookDto);

			return loanDto;

		}).collect(Collectors.toList());

		//TODO estudar Pageable
		return new PageImpl<LoanDTO>(lista,  pegeable, result.getTotalElements());
	}

}
