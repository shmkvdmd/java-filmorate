package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

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
        user.setFriendship(new HashMap<>());
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
            user.setFriendship(userDao.getUser(id).getFriendship());
            userDao.updateUser(user);
            log.info(LogConstants.USER_UPDATED, id);
            return user;
        }
        log.warn(LogConstants.UPDATE_ERROR, id);
        throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, id));
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

    @Override
    public User addFriend(Long userId, Long friendId) {
        validateFriend(userId, friendId);
        User user = userDao.getUser(userId);
        //User friend = userDao.getUser(friendId);
        user.getFriendship().put(friendId, false);
        //friend.getFriends().add(userId);
        log.info(LogConstants.USER_ADD_FRIEND, userId, friendId);
        return user;
    }

    private void validateFriend(Long userId, Long friendId) {
        if (userId < 0 || friendId < 0) {
            throw new ValidationException(ExceptionConstants.NEGATIVE_ID);
        }
        if (userId.equals(friendId)) {
            log.warn(LogConstants.ADD_FRIEND_ERROR_EQUAL_ID);
            throw new ValidationException(ExceptionConstants.ADD_FRIEND_EQUAL);
        }
        if (userDao.getUser(userId) == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, userId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, userId));
        }
        if (userDao.getUser(friendId) == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, friendId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, friendId));
        }
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        validateFriend(userId, friendId);
        User user = userDao.getUser(userId);
        User friend = userDao.getUser(friendId);
        user.getFriendship().remove(friendId);
        friend.getFriendship().remove(userId);
        log.info(LogConstants.USER_DELETE_FRIEND, userId, friendId);
        return user;
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long friendId) {
        validateFriend(userId, friendId);
        Set<Long> intersection = new HashSet<>(userDao.getUser(userId).getFriendship().keySet());
        intersection.retainAll(userDao.getUser(friendId).getFriendship().keySet());
        intersection.remove(userId);
        log.info(LogConstants.COMMON_FRIENDS_REQUEST, userId, friendId);
        return intersection.stream().map(userDao::getUser)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getFriends(Long userId) {
        User user = userDao.getUser(userId);
        if (user == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, userId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, userId));
        }
        log.info(LogConstants.FRIENDS_REQUEST, userId);
        return user.getFriendship().keySet().stream()
                .map(userDao::getUser).collect(Collectors.toSet());
    }

    private Long getNextId() {
        return userDao.getUsers().keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}
