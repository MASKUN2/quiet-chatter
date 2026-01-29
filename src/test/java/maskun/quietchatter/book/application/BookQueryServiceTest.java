package maskun.quietchatter.book.application;

import maskun.quietchatter.WithTestContainerDatabases;
import maskun.quietchatter.book.application.in.Keyword;
import maskun.quietchatter.book.application.out.BookRepository;
import maskun.quietchatter.book.application.out.ExternalBook;
import maskun.quietchatter.book.application.out.ExternalBookSearcher;
import maskun.quietchatter.book.domain.Book;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.ofObject;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookQueryServiceTest implements WithTestContainerDatabases {

    @MockitoBean
    private ExternalBookSearcher externalBookSearcher;

    @MockitoBean
    private BookRepository bookRepository;

    @Autowired
    private BookQueryService bookQueryService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findBy() {
        //given
        ExternalBook expectedExisted = Instancio.of(ExternalBook.class).create();
        ExternalBook notExist1 = Instancio.of(ExternalBook.class).create();
        ExternalBook notExist2 = Instancio.of(ExternalBook.class).create();
        List<ExternalBook> fetchedBooks = List.of(expectedExisted, notExist1, notExist2);

        Book existed = Instancio.of(Book.class)
                .set(field(Book::getId), UUID.randomUUID())
                .set(field(Book::getIsbn), expectedExisted.isbn())
                .set(field(Book::getTitle), expectedExisted.title())
                .create();


        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());

        when(externalBookSearcher.findByKeyword(any(), any()))
                .thenReturn(new SliceImpl<>(fetchedBooks, pageRequest, false));

        when(bookRepository.findByIsbnIn(anySet()))
                .thenReturn(List.of(existed));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> {
                    Book book = invocation.getArgument(0);
                    ofObject(book).fill();
                    return book;
                });

        //when
        Slice<Book> result = bookQueryService.findBy(new Keyword("test"), pageRequest);

        //then
        assertEquals(3, result.getNumberOfElements());
        assertEquals(0, result.getNumber());

        assertThat(result).allSatisfy(book -> assertThat(book.getId()).isNotNull());
        assertThat(result).anyMatch(book -> book.getIsbn().equals(existed.getIsbn()));

        verify(externalBookSearcher).findByKeyword(any(), any());
        verify(bookRepository).findByIsbnIn(anySet());
        verify(bookRepository, times(2)).save(any(Book.class));
    }

}
