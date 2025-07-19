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

    public static final String NEGATIVE_REQUEST_PARAM = "Negative request param";
    public static final String USER_NOT_FOUND_BY_ID = "User with id {} not found";
    public static final String FILM_NOT_FOUND_BY_ID = "Film with id {} not found";
    public static final String ADD_FRIEND_ERROR_EQUAL_ID = "Cant add friend to user. Equal id";
}
