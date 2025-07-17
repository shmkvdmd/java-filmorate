package ru.yandex.practicum.filmorate.constants;

public class ExceptionConstants {
    private ExceptionConstants() {
    }

    public static final String DESCRIPTION_LENGTH_LIMIT = "Превышен размер описания";
    public static final String WRONG_RELEASE_DATE = "Неверная дата релиза";
    public static final String WRONG_FILM_DURATION = "Продолжительность не может быть отрицательной";
    public static final String FILM_NOT_FOUND_BY_ID = "Фильм с id = %d не найден";

    public static final String USER_NOT_FOUND_BY_ID = "Пользователь с id = %d не найден";
    public static final String USER_LOGIN_CANT_CONTAIN_SPACE = "Логин пользователя не может содержать пробелы";
    public static final String WRONG_USER_BIRTHDAY = "Дата рождения не может быть в будущем";
    public static final String EMPTY_ID = "Айди не может отсутствовать";
}
