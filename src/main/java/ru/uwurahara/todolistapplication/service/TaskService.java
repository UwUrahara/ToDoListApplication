package ru.uwurahara.todolistapplication.service;

import ru.uwurahara.todolistapplication.dto.TaskRequestDto;
import ru.uwurahara.todolistapplication.dto.TaskResponseDto;
import ru.uwurahara.todolistapplication.enumerations.SortBy;
import ru.uwurahara.todolistapplication.enumerations.SortDirection;
import ru.uwurahara.todolistapplication.enumerations.Status;

import java.util.List;

public interface TaskService {

    TaskResponseDto create(TaskRequestDto task);

    TaskResponseDto update(int id, TaskRequestDto task);

    void delete(int id);

    List<TaskResponseDto> findAll(Status filterByStatus, SortBy sortBy, SortDirection sortDirection);
}
