package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Map<Long, User> getUsers() {
        return userDao.getUsers();
    }

    @Override
    public User getUser(Long id) {
        User user = userDao.getUser(id);
        if (user == null) {
            log.warn(LogConstants.GET_ERROR, id);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, id));
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validateUser(user);
        Long id = getNextId();
        user.setId(id);
        userDao.createUser(user);
        log.info(LogConstants.USER_ADDED, id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        Long id = user.getId();
        if (id == null) {
            log.warn(LogConstants.UPDATE_ERROR, (Object) null);
            throw new ValidationException(ExceptionConstants.EMPTY_ID);
        }
        validateUser(user);
        if (userDao.getUsers().containsKey(id)) {
            userDao.updateUser(user);
            log.info(LogConstants.USER_UPDATED, id);
            return user;
        }
        log.warn(LogConstants.UPDATE_ERROR, id);
        throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn(LogConstants.USER_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.USER_LOGIN_CANT_CONTAIN_SPACE);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn(LogConstants.USER_VALIDATION_ERROR);
            throw new ValidationException(ExceptionConstants.WRONG_USER_BIRTHDAY);
        }
    }

    private Long getNextId() {
        return userDao.getUsers().keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
