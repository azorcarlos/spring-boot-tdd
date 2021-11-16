package br.com.azor.library.api.payloads;

import br.com.library.api.model.entity.Book;

public class BookPayloadFactor {
	
	public static Book createNewBook() {
		Book bookEntity = Book.builder().title("Meus Livros").author("Azor").isbn("121212").build();
		return bookEntity;
	}
	
	public static Book bookDataBook() {
		Book book = Book.builder().id(11L).title("Meus Livros").author("Azor").isbn("121212").build();
		return book;
	}
	
	
	

}
