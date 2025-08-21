package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.constants.LogConstants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findOneById(id);
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validateUser(user);
        user.setFriendship(new HashMap<>());
        Long id = userRepository.saveUser(user).getId();
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
        userRepository.updateUser(user);
        log.info(LogConstants.USER_UPDATED, id);
        return user;
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
        validateFriendIds(userId, friendId);
        if (!userRepository.existsFriendship(userId, friendId)) {
            userRepository.addFriend(userId, friendId);
        }
        return userRepository.findOneById(userId);
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        validateFriendIds(userId, friendId);
        userRepository.removeFriend(userId, friendId);
        return userRepository.findOneById(userId);
    }

    private void validateFriendIds(Long userId, Long friendId) {
        if (userId < 0 || friendId < 0) {
            throw new ValidationException(ExceptionConstants.NEGATIVE_ID);
        }
        if (Objects.equals(userId, friendId)) {
            log.warn(LogConstants.ADD_FRIEND_ERROR_EQUAL_ID);
            throw new ValidationException(ExceptionConstants.ADD_FRIEND_EQUAL);
        }
        if (userRepository.findOneById(userId) == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, userId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, userId));
        }
        if (userRepository.findOneById(friendId) == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, friendId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, friendId));
        }
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherId) {
        validateFriendIds(userId, otherId);
        List<User> common = userRepository.findCommonFriends(userId, otherId);
        log.info(LogConstants.COMMON_FRIENDS_REQUEST, userId, otherId);
        return new HashSet<>(common);
    }

    @Override
    public Set<User> getFriends(Long userId) {
        User user = userRepository.findOneById(userId);
        if (user == null) {
            log.warn(LogConstants.USER_NOT_FOUND_BY_ID, userId);
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, userId));
        }
        log.info(LogConstants.FRIENDS_REQUEST, userId);
        List<User> friends = userRepository.findFriendsById(userId);
        return new HashSet<>(friends);
    }
}
