package maskun.quietchatter.hexagon.application;

import static maskun.quietchatter.hexagon.domain.Fixture.book;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.ofObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import maskun.quietchatter.hexagon.outbound.ExternalBookSearcher;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BookQueryServiceTest {

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

        Book expectedExisted = book().asNew().create();
        Book notExist1 = book().asNew().create();
        Book notExist2 = book().asNew().create();
        List<Book> fetchedBooks = List.of(expectedExisted, notExist1, notExist2);

        Book existed = Instancio.of(book().asPersisted().toModel())
                .set(Select.field(Book::getIsbn), expectedExisted.getIsbn())
                .create();


        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());

        when(externalBookSearcher.findByKeyword(any(), any()))
                .thenReturn(new PageImpl<>(fetchedBooks, pageRequest, fetchedBooks.size()));

        when(bookRepository.findByIsbnIn(anySet()))
                .thenReturn(List.of(existed));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> {
                    Book book = invocation.getArgument(0);
                    ofObject(book).fill();
                    return book;
                });

        //when
        Page<Book> result = bookQueryService.findBy(new Keyword("test"), pageRequest);

        //then
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getNumberOfElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getNumber());

        assertThat(result).allSatisfy(book -> assertThat(book.getId()).isNotNull());
        assertThat(result).anyMatch(book -> book.equals(existed));

        verify(externalBookSearcher).findByKeyword(any(), any());
        verify(bookRepository).findByIsbnIn(anySet());
        verify(bookRepository, times(2)).save(any(Book.class));
    }

}
