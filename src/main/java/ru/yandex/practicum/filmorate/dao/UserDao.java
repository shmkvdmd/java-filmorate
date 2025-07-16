package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserDao {
    Map<Long, User> getUsers();

    User getUser(Long id);

    void createUser(User user);

    void updateUser(User user);
}
