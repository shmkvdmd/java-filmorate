package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.MockData;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDaoImpl();
        userService = new UserServiceImpl(userDao);
    }

    @Test
    public void shouldThrowExceptionIfLoginContainsSpace() {
        User user = MockData.createUser();
        user.setLogin("log in");
        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    public void shouldThrowExceptionIfBirthdayInFuture() {
        User user = MockData.createUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    public void shouldAcceptValidUserWithEmptyName() {
        User user = MockData.createUser();
        user.setName(null);
        User addedUser = userService.createUser(user);
        assertEquals(1L, addedUser.getId());
        assertEquals(user.getLogin(), addedUser.getName());
        assertTrue(userDao.getUsers().containsValue(addedUser));
    }
}
