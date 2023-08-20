package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class FilmorateApplicationTests {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    void contextLoads() {
    }

    @Test
    public void testCreateUserByIdOrThrow() {
        User testUser = new User(0, "test1@yandex.ru", "test1", "name1", LocalDate.of(2020, 2, 2));

        User testUserWithId = userDbStorage.createUser(testUser);

        User foundUser = userDbStorage.getUserByIdOrThrow(testUserWithId.getId());
        assertEquals("test1@yandex.ru", foundUser.getEmail());
    }

    @Test
    public void testUpdateUser() {
        User testUser = new User(0, "test1@yandex.ru", "test1", "name1", LocalDate.of(2020, 2, 2));
        User testUserWithId = userDbStorage.createUser(testUser);

        testUserWithId.setEmail("test2@yandex.ru");
        userDbStorage.updateUser(testUserWithId);

        User foundUser = userDbStorage.getUserByIdOrThrow(testUserWithId.getId());
        assertEquals("test2@yandex.ru", foundUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        User testUser = new User(0, "test1@yandex.ru", "test1", "name1", LocalDate.of(2020, 2, 2));
        User testUserWithId = userDbStorage.createUser(testUser);

        userDbStorage.deleteUser(testUserWithId.getId());

        assertThrows(ResourceNotFoundException.class, () -> {
            userDbStorage.getUserByIdOrThrow(testUserWithId.getId());
        });
    }

    @Test
    public void testAddFriend() {
        User testUser = new User(0, "test1@yandex.ru", "test1", "name1", LocalDate.of(2020, 2, 2));
        User testFriend = new User(0, "friend1@yandex.ru", "friend1", "friendname1", LocalDate.of(2021, 2, 2));
        User testUserWithId = userDbStorage.createUser(testUser);
        User testFriendWithId = userDbStorage.createUser(testFriend);

        userDbStorage.addFriend(testUserWithId.getId(), testFriendWithId.getId());

        List<User> foundFriends = userDbStorage.getFriends(testUserWithId.getId());
        assertEquals(1, foundFriends.size());
        assertEquals("friend1", foundFriends.get(0).getLogin());
    }

    @Test
    public void testDeleteFriend() {
        User testUser = new User(0, "test1@yandex.ru", "test1", "name1", LocalDate.of(2020, 2, 2));
        User testFriend = new User(0, "friend1@yandex.ru", "friend1", "friendname1", LocalDate.of(2021, 2, 2));
        User testUserWithId = userDbStorage.createUser(testUser);
        User testFriendWithId = userDbStorage.createUser(testFriend);

        userDbStorage.addFriend(testUserWithId.getId(), testFriendWithId.getId());
        userDbStorage.deleteFriend(testUserWithId.getId(), testFriendWithId.getId());

        List<User> foundFriends = userDbStorage.getFriends(testUserWithId.getId());
        assertEquals(0, foundFriends.size());
    }

    @Test
    public void testCreateFilm() {
        Film testFilm = new Film(0, "film1", "desc1", LocalDate.of(2015, 1, 15), 15);
        testFilm.setMpa(new Mpa(1, null));
        testFilm.setGenres(List.of(new Genre(1, null)));
        Film testFilmWithId = filmDbStorage.createFilm(testFilm);

        Film foundFilm = filmDbStorage.getFilmByIdOrThrow(testFilmWithId.getId());

        assertEquals("film1", foundFilm.getName());
    }

    @Test
    public void testUpdateFilm() {
        Film testFilm = new Film(0, "film1", "desc1", LocalDate.of(2015, 1, 15), 15);
        testFilm.setMpa(new Mpa(1, null));
        testFilm.setGenres(List.of(new Genre(1, null)));
        Film testFilmWithId = filmDbStorage.createFilm(testFilm);
        testFilmWithId.setName("film2");
        filmDbStorage.updateFilm(testFilmWithId);

        Film foundFilm = filmDbStorage.getFilmByIdOrThrow(testFilmWithId.getId());

        assertEquals("film2", foundFilm.getName());
    }

    @Test
    public void testDeleteFilm() {
        Film testFilm = new Film(0, "film1", "desc1", LocalDate.of(2015, 1, 15), 15);
        testFilm.setMpa(new Mpa(1, null));
        Film testFilmWithId = filmDbStorage.createFilm(testFilm);

        filmDbStorage.deleteFilm(testFilmWithId.getId());

        assertThrows(ResourceNotFoundException.class, () -> {
            filmDbStorage.getFilmByIdOrThrow(testFilmWithId.getId());
        });
    }

    @Test
    public void testAddAndDeleteLikeToFilm() {
        Film testFilm = new Film(0, "film1", "desc1", LocalDate.of(2015, 1, 15), 15);
        testFilm.setMpa(new Mpa(1, null));
        testFilm.setGenres(List.of(new Genre(1, null)));
        Film testFilm2 = new Film(0, "film2", "desc1", LocalDate.of(2015, 1, 15), 15);
        testFilm2.setMpa(new Mpa(2, null));
        testFilm2.setGenres(List.of(new Genre(2, null), new Genre(3, null)));
        User testUser = new User(0, "test1@yandex.ru", "test1", "name1", LocalDate.of(2020, 2, 2));
        Film testFilmWithId = filmDbStorage.createFilm(testFilm);
        Film testFilm2WithId = filmDbStorage.createFilm(testFilm2);
        User testUserWithId = userDbStorage.createUser(testUser);

        filmDbStorage.addLikeToFilm(testFilmWithId.getId(), testUserWithId.getId());

        List<Film> topFilms = filmDbStorage.getTopFilms(3);

        assertEquals(2, topFilms.size());
        assertEquals(testFilmWithId.getId(), topFilms.get(0).getId());

        filmDbStorage.deleteLikeToFilm(testFilmWithId.getId(), testUserWithId.getId());
        filmDbStorage.addLikeToFilm(testFilm2WithId.getId(), testUserWithId.getId());

        List<Film> topFilms2 = filmDbStorage.getTopFilms(3);

        assertEquals(2, topFilms2.size());
        assertEquals(testFilm2WithId.getId(), topFilms2.get(0).getId());
    }


}
