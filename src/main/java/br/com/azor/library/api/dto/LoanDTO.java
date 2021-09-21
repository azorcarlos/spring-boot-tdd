package br.com.azor.library.api.dto;

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
	private String customer;
	private String isbn;
	private BookDTO book;

}
