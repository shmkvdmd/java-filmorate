package ru.yandex.practicum.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import(MpaRepositoryImpl.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaRepositoryImplTest {

    private final MpaRepositoryImpl mpaRepository;

    @Test
    void shouldReturnAllMpa() {
        List<Mpa> mpaList = mpaRepository.findAll();
        assertNotNull(mpaList);
        assertEquals(5, mpaList.size());
        assertTrue(mpaList.stream().anyMatch(m -> "G".equals(m.getName())));
    }

    @Test
    void shouldReturnMpa() {
        Mpa mpa = mpaRepository.findOneById(1L);
        assertNotNull(mpa);
        assertEquals(1L, mpa.getId());
        assertEquals("G", mpa.getName());
    }

    @Test
    void shouldThrowWhenNotExists() {
        assertThrows(RuntimeException.class, () -> mpaRepository.findOneById(9999L));
    }
}