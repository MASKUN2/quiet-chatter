package maskun.quietchatter.hexagon.application;

import maskun.quietchatter.hexagon.domain.Book;
import maskun.quietchatter.hexagon.domain.value.Isbn;
import maskun.quietchatter.hexagon.domain.value.Title;
import maskun.quietchatter.hexagon.inbound.BookService;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book Save(Title title){
        Book book = Book.newOf(title, new Isbn("9788937460500"));
        return bookRepository.save(book);
    }
}
