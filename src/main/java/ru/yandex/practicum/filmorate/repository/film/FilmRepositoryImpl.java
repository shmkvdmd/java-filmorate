package ru.yandex.practicum.filmorate.repository.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constants.ExceptionConstants;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@Repository
@AllArgsConstructor
public class FilmRepositoryImpl implements FilmRepository {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    @Override
    public Film saveFilm(Film film) {
        checkMpaAndGenre(film);

        String insertSql = "INSERT INTO film (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        var keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, (int) film.getDuration().toMinutes());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException(ExceptionConstants.ERROR_SAVE_FILM);
        }
        Long filmId = key.longValue();

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenres(filmId, film.getGenres());
        }

        return findOneById(filmId);
    }

    private void checkMpaAndGenre(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaRepository.findOneById(film.getMpa().getId());
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre g : film.getGenres()) {
                if (g == null || g.getId() == null) {
                    throw new NotFoundException(ExceptionConstants.EMPTY_GENRE);
                }
                genreRepository.findById(g.getId());
            }
        }
    }

    @Override
    public Film updateFilm(Film film) {
        checkMpaAndGenre(film);

        String updateSql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        int updated = jdbcTemplate.update(updateSql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId(),
                film.getId());
        if (updated == 0) {
            throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, film.getId()));
        }
        updateGenres(film.getId(), film.getGenres() == null ? Set.of() : film.getGenres());
        return findOneById(film.getId());
    }

    @Override
    public Film findOneById(Long id) {
        Film film;
        String sql = "SELECT f.id AS id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name " +
                "FROM film f LEFT JOIN mpa m ON f.mpa_id = m.id WHERE f.id = ?";
        try {
            film = jdbcTemplate.queryForObject(sql, (rs, rn) -> {
                Film.FilmBuilder builder = Film.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(Duration.ofMinutes(rs.getInt("duration")));

                Long mpaId = rs.getLong("mpa_id");
                if (!rs.wasNull()) {
                    String mpaName = rs.getString("mpa_name");
                    builder.mpa(new Mpa(mpaId, mpaName));
                } else {
                    builder.mpa(null);
                }

                return builder.build();
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format(ExceptionConstants.FILM_NOT_FOUND_BY_ID, id));
        }

        film.setGenres(new LinkedHashSet<>(genreRepository.findGenresByFilmId(id)));

        List<Long> likes = jdbcTemplate.queryForList("SELECT user_id FROM film_user WHERE film_id = ?", Long.class, id);
        film.setUserIdLikes(new LinkedHashSet<>(likes));
        return film;
    }

    @Override
    public List<Film> findAll() {
        String sql = """
                SELECT f.id AS film_id, f.name AS film_name, f.description, f.release_date, f.duration,
                       f.mpa_id AS mpa_id,
                       g.id   AS genre_id, g.name AS genre_name,
                       fu.user_id AS like_user_id
                FROM film f
                LEFT JOIN film_genre fg ON f.id = fg.film_id
                LEFT JOIN genre g ON fg.genre_id = g.id
                LEFT JOIN film_user fu ON f.id = fu.film_id
                ORDER BY f.id;
                """;

        return jdbcTemplate.query(sql, (ResultSet rs) -> {
            Map<Long, Film> map = new LinkedHashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Film film = map.get(filmId);
                if (film == null) {
                    Long mpaId = rs.getObject("mpa_id") == null ? null : rs.getLong("mpa_id");
                    Mpa mpa = null;
                    if (mpaId != null) {
                        mpa = mpaRepository.findOneById(mpaId);
                    }

                    film = Film.builder()
                            .id(filmId)
                            .name(rs.getString("film_name"))
                            .description(rs.getString("description"))
                            .releaseDate(rs.getDate("release_date").toLocalDate())
                            .duration(Duration.ofMinutes(rs.getInt("duration")))
                            .mpa(mpa)
                            .genres(new LinkedHashSet<>())
                            .userIdLikes(new LinkedHashSet<>())
                            .build();
                    map.put(filmId, film);
                }

                Long genreId = rs.getObject("genre_id") == null ? null : rs.getLong("genre_id");
                if (genreId != null) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }

                Long likeUserId = rs.getObject("like_user_id") == null ? null : rs.getLong("like_user_id");
                if (likeUserId != null) {
                    film.getUserIdLikes().add(likeUserId);
                }
            }
            return new ArrayList<>(map.values());
        });
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Integer exists = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM film_user WHERE film_id = ? AND user_id = ?",
                Integer.class, filmId, userId);
        if (exists != null && exists > 0) return;
        jdbcTemplate.update("INSERT INTO film_user (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update("DELETE FROM film_user WHERE film_id = ? AND user_id = ?", filmId, userId);
    }

    @Override
    public int getLikesCount(Long filmId) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM film_user WHERE film_id = ?", Integer.class, filmId);
        return count == null ? 0 : count;
    }

    @Override
    public void addGenres(Long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;
        List<Long> genreIds = genres.stream()
                .filter(g -> g != null && g.getId() != null)
                .map(Genre::getId)
                .toList();

        if (genreIds.isEmpty()) return;
        String existsSql = "SELECT genre_id FROM film_genre WHERE film_id = ? AND genre_id IN (?)";
        List<Long> existingGenreIds = jdbcTemplate.queryForList(existsSql, Long.class, filmId,
                String.join(",", genreIds.stream().map(String::valueOf).toList()));

        List<Long> newGenreIds = genreIds.stream()
                .filter(id -> !existingGenreIds.contains(id))
                .toList();

        if (newGenreIds.isEmpty()) return;

        String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, newGenreIds.get(i));
            }

            @Override
            public int getBatchSize() {
                return newGenreIds.size();
            }
        });
    }

    @Override
    public void updateGenres(Long filmId, Set<Genre> genres) {
        deleteGenres(filmId);
        addGenres(filmId, genres);
    }

    @Override
    public Set<Genre> getGenres(Long filmId) {
        return new LinkedHashSet<>(jdbcTemplate.query(
                "SELECT g.id AS genre_id, g.name AS name FROM genre g JOIN film_genre fg ON g.id = fg.genre_id " +
                        "WHERE fg.film_id = ? ORDER BY g.id",
                (ResultSet rs, int rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }, filmId));
    }

    @Override
    public void deleteGenres(Long filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", filmId);
    }
}
