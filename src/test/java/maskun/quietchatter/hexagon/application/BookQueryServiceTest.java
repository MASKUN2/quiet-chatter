package maskun.quietchatter.hexagon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import maskun.quietchatter.hexagon.application.value.Keyword;
import maskun.quietchatter.hexagon.domain.book.Book;
import maskun.quietchatter.hexagon.domain.book.BookFixture;
import maskun.quietchatter.hexagon.outbound.BookRepository;
import maskun.quietchatter.hexagon.outbound.ExternalBookSearcher;
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
        Book expectedExisted = BookFixture.builder().random().id(null).build();
        Book notExist1 = BookFixture.builder().random().id(null).build();
        Book notExist2 = BookFixture.builder().random().id(null).build();
        List<Book> fetchedBooks = List.of(expectedExisted, notExist1, notExist2);

        Book existed = BookFixture.builder().random()
                .isbn(expectedExisted.getIsbn()).build();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());

        when(externalBookSearcher.findByKeyword(any(), any()))
                .thenReturn(new PageImpl<>(fetchedBooks, pageRequest, fetchedBooks.size()));

        when(bookRepository.findByIsbnIn(anySet()))
                .thenReturn(List.of(existed));

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> {
                    Book book = invocation.getArgument(0);
                    return BookFixture.builder(book).randomId().build();
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
