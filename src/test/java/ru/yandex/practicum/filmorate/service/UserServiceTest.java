//package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.MockData;
//import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
//import ru.yandex.practicum.filmorate.exceptions.ValidationException;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.user.UserService;
//import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
//
//import java.time.LocalDate;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class UserServiceTest {
//    private UserService userService;
//    private UserDao userDao;
//
//    @BeforeEach
//    public void setUp() {
//        userDao = new UserDaoImpl();
//        userService = new UserServiceImpl(userDao);
//    }
//
//    @Test
//    public void shouldThrowExceptionIfLoginContainsSpace() {
//        User user = MockData.createUser();
//        user.setLogin("log in");
//        assertThrows(ValidationException.class, () -> userService.createUser(user));
//    }
//
//    @Test
//    public void shouldThrowExceptionIfBirthdayInFuture() {
//        User user = MockData.createUser();
//        user.setBirthday(LocalDate.now().plusDays(1));
//        assertThrows(ValidationException.class, () -> userService.createUser(user));
//    }
//
//    @Test
//    public void shouldAcceptValidUserWithEmptyName() {
//        User user = MockData.createUser();
//        user.setName(null);
//        User addedUser = userService.createUser(user);
//        assertEquals(1L, addedUser.getId());
//        assertEquals(user.getLogin(), addedUser.getName());
//        assertTrue(userDao.getUsers().containsValue(addedUser));
//    }
//
//    @Test
//    public void shouldAddFriend() {
//        User user1 = userService.createUser(MockData.createUser());
//        User user2 = userService.createUser(MockData.createUser());
//        userService.addFriend(1L, 2L);
//        assertTrue(user1.getFriends().contains(2L));
//        assertEquals(1, user1.getFriends().size());
//        assertTrue(user2.getFriends().contains(1L));
//    }
//
//    @Test
//    public void shouldThrowExceptionIfUserNotFoundWhenAddingFriend() {
//        userService.createUser(MockData.createUser());
//        assertThrows(NotFoundException.class, () -> userService.addFriend(1L, 999L));
//    }
//
//    @Test
//    public void shouldThrowExceptionIfUserEqualAddingFriend() {
//        userService.createUser(MockData.createUser());
//        assertThrows(ValidationException.class, () -> userService.addFriend(1L, 1L));
//    }
//
//    @Test
//    public void shouldDeleteFriend() {
//        User user1 = userService.createUser(MockData.createUser());
//        User user2 = userService.createUser(MockData.createUser());
//        userService.addFriend(1L, 2L);
//        userService.deleteFriend(1L, 2L);
//        assertFalse(user1.getFriends().contains(2L));
//        assertEquals(0, user1.getFriends().size());
//        assertFalse(user2.getFriends().contains(1L));
//    }
//
//    @Test
//    public void shouldThrowExceptionIfUserNotFoundWhenDeletingFriend() {
//        userService.createUser(MockData.createUser());
//        assertThrows(NotFoundException.class, () -> userService.deleteFriend(1L, 999L));
//    }
//
//    @Test
//    public void shouldReturnCommonFriends() {
//        userService.createUser(MockData.createUser());
//        userService.createUser(MockData.createUser());
//        User user3 = userService.createUser(MockData.createUser());
//        userService.addFriend(1L, 3L);
//        userService.addFriend(2L, 3L);
//        Set<User> commonFriends = userService.getCommonFriends(1L, 2L);
//        assertEquals(1, commonFriends.size());
//        assertTrue(commonFriends.contains(user3));
//    }
//
//    @Test
//    public void shouldReturnFriends() {
//        userService.createUser(MockData.createUser());
//        User user2 = userService.createUser(MockData.createUser());
//        userService.addFriend(1L, 2L);
//        Set<User> friends = userService.getFriends(1L);
//        assertEquals(1, friends.size());
//        assertTrue(friends.contains(user2));
//    }
//}
