package br.com.library.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.library.api.exception.ApiErrors;
import br.com.library.api.exception.BusinessException;

@RestControllerAdvice
public class ApplicationControllerAdvice {
	
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
		
		@ExceptionHandler(ResponseStatusException.class)
		public ResponseEntity<ApiErrors> handleResponseStatusException(ResponseStatusException ex) {
			return new ResponseEntity<>(new ApiErrors(ex), ex.getStatus());
		}

}
