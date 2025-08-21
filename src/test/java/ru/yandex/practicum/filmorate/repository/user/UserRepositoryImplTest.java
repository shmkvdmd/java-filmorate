package ru.yandex.practicum.filmorate.repository.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.MockData.createUser;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserRepositoryImpl.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    void shouldSaveAndFindById() {
        User user = createUser();
        User saved = userRepository.saveUser(user);
        assertNotNull(saved.getId());
        User found = userRepository.findOneById(saved.getId());
        assertEquals(saved.getEmail(), found.getEmail());
        assertEquals(saved.getLogin(), found.getLogin());
    }

    @Test
    void shouldUpdateUser() {
        User user = createUser();
        User saved = userRepository.saveUser(user);
        saved.setName("NewName");
        User updated = userRepository.updateUser(saved);
        assertEquals("NewName", updated.getName());
    }

    @Test
    void shouldFindAllUsers() {
        userRepository.saveUser(createUser());
        userRepository.saveUser(createUser("email@mail.ru", "anotherlogin", "name",
                LocalDate.now().minusYears(20)));
        List<User> all = userRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void shouldThrowWhenFindOneByIdNotFound() {
        assertThrows(RuntimeException.class, () -> userRepository.findOneById(100L));
    }
}
