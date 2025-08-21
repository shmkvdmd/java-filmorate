package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getUsers();

    User getUser(Long id);

    User createUser(User user);

    User updateUser(User user);

    User addFriend(Long userId, Long friendId);

    User deleteFriend(Long userId, Long friendId);

    Set<User> getCommonFriends(Long userId, Long friendId);

    Set<User> getFriends(Long userId);

}
