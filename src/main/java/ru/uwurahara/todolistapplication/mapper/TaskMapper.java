package ru.uwurahara.todolistapplication.mapper;

import ru.uwurahara.todolistapplication.dto.TaskResponseDto;
import ru.uwurahara.todolistapplication.model.Task;

public interface TaskMapper {
    static TaskResponseDto mapToTaskResponseDto(Task task) {
        return new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.getDeadline(), task.getStatus());
    }
}
