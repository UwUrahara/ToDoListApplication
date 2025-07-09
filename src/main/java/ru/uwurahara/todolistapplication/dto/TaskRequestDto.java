package ru.uwurahara.todolistapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.uwurahara.todolistapplication.enumerations.Status;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TaskRequestDto {

    @NotNull(message = "Название задачи не может быть пустым")
    @NotBlank(message = "Название задачи не может быть пустым")
    private final String title;

    private final String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotNull(message = "Дедлайн не может быть пустым")
    @FutureOrPresent(message = "Дедлайн не может быть в прошлом")
    private final LocalDate deadline;

    private final Status status;
}
