package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
@Builder
public class User {
    private Long id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String login;

    private String name;

    @NotNull
    private LocalDate birthday;

    @NotNull
    private Set<Friendship> friendshipSet;

    @Data
    @EqualsAndHashCode(of ="friendId")
    @AllArgsConstructor
    class Friendship {
        @NotNull
        private Long friendId;

        @NotNull
        private Boolean isConfirmed;
    }
}
