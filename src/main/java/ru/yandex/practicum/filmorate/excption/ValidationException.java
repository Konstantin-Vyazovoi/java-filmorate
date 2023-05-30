package ru.yandex.practicum.filmorate.excption;

public class ValidationException extends Exception {
    public ValidationException(String massage) {
        super(massage);
    }
}
