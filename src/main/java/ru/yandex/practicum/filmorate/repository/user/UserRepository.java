package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(User user);

    User findOneById(Long id);

    List<User> findAll();

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    boolean existsFriendship(Long userId, Long friendId);

    List<User> findFriendsById(Long id);

    List<User> findCommonFriends(Long id, Long otherId);
}
