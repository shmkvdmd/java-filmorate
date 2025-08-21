package ru.yandex.practicum.filmorate.repository.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRepositoryImpl.class})
class GenreRepositoryImplTest {
    private final GenreRepository genreRepository;

    @Test
    public void shouldFindAllGenres() {
        List<Genre> genreList = genreRepository.findAll();
        assertNotNull(genreList);
        assertEquals(6, genreList.size());
        assertTrue(genreList.stream().anyMatch(g -> "Комедия".equals(g.getName())));
    }

    @Test
    public void shouldFindGenre() {
        Genre genre = genreRepository.findById(1L);
        assertNotNull(genre);
        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    void shouldThrowWhenNotExists() {
        assertThrows(RuntimeException.class, () -> genreRepository.findById(9999L));
    }
}