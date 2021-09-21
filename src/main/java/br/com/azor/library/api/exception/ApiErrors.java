package br.com.azor.library.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

public class ApiErrors {

	List<String> errors;

	public ApiErrors(BindingResult bindingResult) {
		this.errors = new ArrayList<String>();
		bindingResult.getAllErrors().forEach(erro -> this.errors.add(erro.getDefaultMessage()));
	}

	public ApiErrors(BusinessException ex) {
		this.errors = Arrays.asList(ex.getMessage());
	}

	public ApiErrors(ResponseStatusException ex) {
		this.errors = Arrays.asList(ex.getReason());
	}

	public List<String> getErrors() {
		return errors;
	}

}
