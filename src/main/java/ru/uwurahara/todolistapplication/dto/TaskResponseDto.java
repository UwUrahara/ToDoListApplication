package ru.uwurahara.todolistapplication.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.uwurahara.todolistapplication.enumerations.Status;

import java.time.LocalDate;

public class TaskResponseDto {
    private final int id;

    private final String title;

    private final String description;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private final LocalDate deadline;

    private final Status status;

    public TaskResponseDto(int id, String title, String description, LocalDate deadline, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.status = status;
    }

    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public LocalDate getDeadline() { return deadline; }

    public Status getStatus() { return status; }
}
