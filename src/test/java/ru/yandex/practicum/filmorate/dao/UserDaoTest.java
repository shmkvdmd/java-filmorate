package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.MockData;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDaoImpl();
    }

    @Test
    public void shouldAddAndRetrieveUser() {
        User user = MockData.createUser();
        user.setId(1L);
        userDao.createUser(user);
        assertTrue(userDao.getUsers().containsValue(user));
        assertEquals(user, userDao.getUser(1L));
    }

    @Test
    public void shouldUpdateUser() {
        User user = MockData.createUser();
        user.setId(1L);
        userDao.createUser(user);
        User updatedUser = MockData.createUser();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        userDao.updateUser(updatedUser);
        assertEquals(updatedUser, userDao.getUser(1L));
    }
}
