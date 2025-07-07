package ru.uwurahara.todolistapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.uwurahara.todolistapplication.enumerations.Status;

import java.time.LocalDate;

public class TaskRequestDto {

    private final String title;

    private final String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private final LocalDate deadline;

    private final Status status;

    public TaskRequestDto(String title, String description, LocalDate deadline, Status status) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }


    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public LocalDate getDeadline() { return deadline; }

    public Status getStatus() { return status; }
}
