package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserDao userDao = new UserDaoImpl();

    @GetMapping
    public Collection<User> getUsers() {
        return userDao.getUsers().values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        userDao.createUser(user);
        return userDao.getUser(user.getId());
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userDao.updateUser(user);
        return userDao.getUser(user.getId());
    }
}
