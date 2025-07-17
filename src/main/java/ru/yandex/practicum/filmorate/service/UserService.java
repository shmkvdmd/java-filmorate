package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserService {
    Map<Long, User> getUsers();

    User getUser(Long id);

    User createUser(User user);

    User updateUser(User user);
}
