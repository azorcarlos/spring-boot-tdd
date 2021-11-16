package br.com.library.api.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDTO {

	private Long id;
	@NotEmpty
	private String customer;

	@NotEmpty
	private String email;

	@NotEmpty
	private String isbn;

	private BookDTO book;

}
