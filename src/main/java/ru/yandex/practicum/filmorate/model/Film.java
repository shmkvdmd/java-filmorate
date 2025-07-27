package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.model.enums.Genre;
import ru.yandex.practicum.filmorate.model.enums.MPA;
import ru.yandex.practicum.filmorate.model.json.DurationDeserializer;
import ru.yandex.practicum.filmorate.model.json.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Builder
public class Film {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

    @NotNull
    private Set<Genre> genreSet;

    @NotNull
    private MPA mpa;
}
