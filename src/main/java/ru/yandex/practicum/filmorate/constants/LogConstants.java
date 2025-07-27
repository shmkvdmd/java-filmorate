package ru.yandex.practicum.filmorate.constants;

public class LogConstants {
    private LogConstants() {
    }

    public static final String GET_ERROR = "Get error. Wrong id: {}}";
    public static final String FILM_ADDED = "Film with id {} added";
    public static final String FILM_UPDATED = "Film with id {} updated";
    public static final String FILM_VALIDATION_ERROR = "Film is not valid";
    public static final String UPDATE_ERROR = "Update error. Wrong id: {}";
    public static final String USER_ADDED = "User with id {} added";
    public static final String USER_UPDATED = "User with id {} updated";
    public static final String USER_VALIDATION_ERROR = "User is not valid";
    public static final String FILM_LIKE = "User with id {} added like to film with id {}";
    public static final String FILM_DELETE_LIKE = "User with id {} deleted like from film with id {}";
    public static final String USER_ADD_FRIEND = "User with id {} added friend with id {}";
    public static final String USER_DELETE_FRIEND = "User with id {} deleted friend with id {}";
    public static final String POPULAR_FILMS_REQUEST = "Request popular films with param count value {}";
    public static final String COMMON_FRIENDS_REQUEST = "Request common friends of {} and {}";
    public static final String FRIENDS_REQUEST = "Request friends of user with id {}";
    public static final String NEGATIVE_REQUEST_PARAM = "Negative request param";
    public static final String USER_NOT_FOUND_BY_ID = "User with id {} not found";
    public static final String FILM_NOT_FOUND_BY_ID = "Film with id {} not found";
    public static final String ADD_FRIEND_ERROR_EQUAL_ID = "Cant add friend to user. Equal id";
}
