package ru.yandex.practicum.filmorate.excption;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String massage) {
        super(massage);
    }
}
