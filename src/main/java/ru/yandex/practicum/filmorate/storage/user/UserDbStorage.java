package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query("select * from users", rowMapperUser());
        return users;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", user.getName(),
                "email", user.getEmail(),
                "login", user.getLogin(),
                "birthday", user.getBirthday().toString());
        Integer id = insert.execute(params);
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("UPDATE USERS SET NAME = ?, " +
                "EMAIL = ?," +
                "LOGIN = ?," +
                "BIRTHDAY = ? " +
                "WHERE id = ?",
                user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void deleteUser(User user) {
        jdbcTemplate.update("delete from users where id = ?", user.getId());
    }

    @Override
    public User getUserByID(Integer id) {
        List<User> users = jdbcTemplate.query("select * from users where id = ?", rowMapperUser(), id);
        if (users.size() == 0) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return users.get(0);
    }

    @Override
    public ArrayList<User> getCommonFriends(int userID, int otherUserID) {
        return null;
    }

    @Override
    public ArrayList<User> getAllFriends(int userID) {
        return null;
    }

    @Override
    public User addFriend(int userID, int friendID) throws ValidationException {
        return null;
    }

    @Override
    public User deleteFriend(int userID, int friendID) {
        return null;
    }

    private LocalDate dateToLocalDate(Date date) {
        LocalDate localDate = null;
        if (date != null) {
            long year = date.getTime();
            int m = date.getMonth();
            int d = date.getDay();
            int dat = date.getDate();
            localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return localDate;
    }

    private RowMapper<User> rowMapperUser() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(UserDbStorage.this.dateToLocalDate(rs.getDate("birthday")))
                .build();
    }
}
