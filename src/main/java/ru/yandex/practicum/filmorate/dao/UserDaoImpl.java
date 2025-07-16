package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.warn(LogConstants.GET_ERROR, id);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, id));
        }
        return user;
    }

    @Override
    public void createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (isCorrectUser(user)) {
            Long id = getNextId();
            user.setId(id);
            users.put(id, user);
            log.info(LogConstants.USER_ADDED, id);
        }
    }

    @Override
    public void updateUser(User user) {
        Long id = user.getId();
        if (id == null) {
            log.warn(LogConstants.UPDATE_ERROR, (Object) null);
            throw new ValidationException(ExceptionConstants.EMPTY_ID);
        }
        if (users.containsKey(id) && isCorrectUser(user)) {
            users.put(id, user);
            log.info(LogConstants.USER_UPDATED, id);
            return;
        }
        log.warn(LogConstants.UPDATE_ERROR, id);
        throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
    }

    private boolean isCorrectUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn(LogConstants.USER_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.USER_LOGIN_CANT_CONTAIN_SPACE);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn(LogConstants.USER_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.WRONG_USER_BIRTHDAY);
        }
        return true;
    }

    private Long getNextId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
