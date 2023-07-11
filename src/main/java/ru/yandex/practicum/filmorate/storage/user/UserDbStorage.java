package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
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
        Integer id = (Integer) insert.executeAndReturnKey(params);
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int update = jdbcTemplate.update("UPDATE USERS SET NAME = ?, " +
                        "EMAIL = ?," +
                        "LOGIN = ?," +
                        "BIRTHDAY = ? " +
                        "WHERE id = ?",
                user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        if (update == 0) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return user;
    }

    @Override
    public void deleteUser(User user) {
        jdbcTemplate.update("delete from users where id = ?", user.getId());
    }

    @Override
    public User getUserByID(Integer id) {
        List<User> users = getQuery("select * from users where id = ?", id);
        if (users.isEmpty()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return users.get(0);
    }

    @Override
    public ArrayList<User> getCommonFriends(int userID, int otherUserID) {
        List<User> users = jdbcTemplate.query("SELECT u.ID , u.NAME , u.EMAIL , u.LOGIN , u.BIRTHDAY " +
                        "FROM FRIENDS f JOIN USERS u ON f.FRIEND_ID = u.ID " +
                        "WHERE USER_ID IN (?, ?) AND FRIEND_ID NOT IN (?, ?) " +
                        "GROUP BY u.ID",
                rowMapperUser(),
                userID, otherUserID, userID, otherUserID);
        return (ArrayList<User>) users;
    }

    @Override
    public ArrayList<User> getAllFriends(int userID) {
        List<User> users = getQuery("SELECT u.ID, u.NAME, u.EMAIL, u.LOGIN, u.BIRTHDAY " +
                "FROM FRIENDS f JOIN USERS u ON f.FRIEND_ID = u.ID " +
                "WHERE f.USER_ID = ?", userID);
        return (ArrayList<User>) users;
    }

    @Override
    public void addFriend(int userID, int friendID) throws ValidationException {
        jdbcTemplate.update("INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)", userID, friendID);

    }

    @Override
    public void deleteFriend(int userID, int friendID) {
        jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?", userID, friendID);
    }

    public void deleteUsers() {
        jdbcTemplate.update("delete from users");
    }

    private RowMapper<User> rowMapperUser() {
        return (rs, rowNum) -> User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    private List<User> getQuery(String sql, int id) {
        return jdbcTemplate.query(sql, rowMapperUser(), id);
    }
}
