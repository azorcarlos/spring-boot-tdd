package br.com.azor.library.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.azor.library.api.dto.BookDTO;
import br.com.azor.library.api.exception.ApiErrors;
import br.com.azor.library.api.exception.BusinessException;
import br.com.azor.library.api.model.entity.Book;
import br.com.azor.library.api.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private BookService service;
	private ModelMapper modelMapper;

	public BookController(BookService service, ModelMapper modelMapper) {
		this.service = service;
		this.modelMapper = modelMapper;

	}

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public BookDTO create(@RequestBody @Valid BookDTO dto) {

		Book entity = modelMapper.map(dto, Book.class);

		entity = service.save(entity);

		return modelMapper.map(entity, BookDTO.class);

	}

	@GetMapping("{id}")
	public BookDTO get(@PathVariable Long id) {

		return service.findById(id).map(book -> modelMapper.map(service.findById(id).get(), BookDTO.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

	}

	@GetMapping
	public Page<BookDTO> findAll(BookDTO dto, Pageable pageRequest) {
		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);

		List<BookDTO> list = result.getContent().stream().map(entity -> modelMapper.map(entity, BookDTO.class))
				.collect(Collectors.toList());

		return new PageImpl<BookDTO>(list, (Pageable) pageRequest,
				result.getTotalElements());

	}
	
	@PutMapping("{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public BookDTO update(@PathVariable Long id, @RequestBody BookDTO bookDTO) {

		Book book = service.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			 book.setAuthor(bookDTO.getAuthor());
			 book.setTitle(bookDTO.getTitle());
		service.update(book);

		return modelMapper.map(book, BookDTO.class);

	}
	
	
	@DeleteMapping("{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable Long id) {

		Book book = service.findById(id)
				.orElseThrow(() ->new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.deleteById(book.getId());

	}
	
	
	
	//Exceptions
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptions(MethodArgumentNotValidException ex) {
		BindingResult bindigResult = ex.getBindingResult();

		return new ApiErrors(bindigResult);

	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessException(BusinessException ex) {
		return new ApiErrors(ex);

	}
	

}
