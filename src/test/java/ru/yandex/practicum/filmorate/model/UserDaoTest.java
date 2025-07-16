package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDaoTest {
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDaoImpl();
    }

    @Test
    public void shouldThrowExceptionIfLoginContainsSpace() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("log in")
                .name("Name")
                .birthday(LocalDate.now())
                .build();
        assertThrows(ValidationException.class, () -> userDao.createUser(user));
    }

    @Test
    public void shouldThrowExceptionIfBirthdayInFuture() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("login")
                .name("Name")
                .birthday(LocalDate.now().plusDays(1))
                .build();
        assertThrows(ValidationException.class, () -> userDao.createUser(user));
    }

    @Test
    public void shouldAcceptValidUserWithEmptyName() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("login")
                .name("")
                .birthday(LocalDate.now().minusYears(20))
                .build();
        userDao.createUser(user);
        assert userDao.getUsers().containsValue(user);
    }
}
