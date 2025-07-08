package ru.uwurahara.todolistapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.uwurahara.todolistapplication.enumerations.Status;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TaskRequestDto {

    private final String title;

    private final String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private final LocalDate deadline;

    private final Status status;
}
