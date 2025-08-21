package ru.yandex.practicum.filmorate.repository.user;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User saveUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
            user.setFriendship(new HashMap<>());
            return user;
        } else {
            throw new RuntimeException(ExceptionConstants.ERROR_SAVE_USER);
        }
    }

    @Override
    public User updateUser(User user) {
        int updated = jdbcTemplate.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        if (updated == 0) {
            throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, user.getId()));
        }
        return findOneById(user.getId());
    }

    @Override
    public User findOneById(Long id) {
        String sql = "SELECT u.id AS id, u.email, u.login, u.name, u.birthday, f.friend_id, f.is_confirmed " +
                "FROM users u " +
                "LEFT JOIN friendship f ON u.id = f.user_id " +
                "WHERE u.id = ?";

        return jdbcTemplate.query(sql, (ResultSet rs) -> {
            User user = null;
            Map<Long, Boolean> friends = new HashMap<>();
            while (rs.next()) {
                if (user == null) {
                    user = User.builder()
                            .id(rs.getLong("id"))
                            .email(rs.getString("email"))
                            .login(rs.getString("login"))
                            .name(rs.getString("name"))
                            .birthday(rs.getDate("birthday").toLocalDate())
                            .friendship(new HashMap<>())
                            .build();
                }
                Long friendId = rs.getObject("friend_id") != null ? rs.getLong("friend_id") : null;
                if (friendId != null) {
                    Boolean confirmed = rs.getObject("is_confirmed") != null ? rs.getBoolean("is_confirmed") : Boolean.FALSE;
                    friends.put(friendId, confirmed);
                }
            }
            if (user == null) {
                throw new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, id));
            }
            user.setFriendship(friends);
            return user;
        }, id);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT u.id AS id, u.email, u.login, u.name, u.birthday, f.friend_id, f.is_confirmed " +
                "FROM users u " +
                "LEFT JOIN friendship f ON u.id = f.user_id " +
                "ORDER BY u.id";
        return jdbcTemplate.query(sql, (ResultSet rs) -> {
            Map<Long, User> map = new LinkedHashMap<>();
            while (rs.next()) {
                Long userId = rs.getLong("id");
                User user = map.get(userId);
                if (user == null) {
                    user = User.builder()
                            .id(userId)
                            .email(rs.getString("email"))
                            .login(rs.getString("login"))
                            .name(rs.getString("name"))
                            .birthday(rs.getDate("birthday").toLocalDate())
                            .friendship(new HashMap<>())
                            .build();
                    map.put(userId, user);
                }
                Long friendId = rs.getObject("friend_id") != null ? rs.getLong("friend_id") : null;
                if (friendId != null) {
                    Boolean confirmed = rs.getObject("is_confirmed") != null ?
                            rs.getBoolean("is_confirmed") : Boolean.FALSE;
                    user.getFriendship().put(friendId, confirmed);
                }
            }
            return new ArrayList<>(map.values());
        });
    }

    @Override
    public boolean existsFriendship(Long userId, Long friendId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM friendship WHERE user_id = ? AND friend_id = ?",
                Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (existsFriendship(userId, friendId)) {
            return;
        }
        jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, is_confirmed) VALUES (?, ?, ?)",
                userId, friendId, true);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }

    @Override
    public List<User> findFriendsById(Long id) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM users u " +
                "WHERE u.id IN (SELECT friend_id FROM friendship WHERE user_id = ? AND is_confirmed = true)";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id);
    }

    @Override
    public List<User> findCommonFriends(Long id, Long otherId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM users u " +
                "WHERE u.id IN (SELECT friend_id FROM friendship WHERE user_id = ? AND is_confirmed = true " +
                "AND friend_id IN (SELECT friend_id FROM friendship WHERE user_id = ? AND is_confirmed = true))";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id, otherId);
    }
}
