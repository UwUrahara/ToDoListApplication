package ru.uwurahara.todolistapplication.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.uwurahara.todolistapplication.dto.TaskRequestDto;
import ru.uwurahara.todolistapplication.dto.TaskResponseDto;
import ru.uwurahara.todolistapplication.enumerations.SortBy;
import ru.uwurahara.todolistapplication.enumerations.SortDirection;
import ru.uwurahara.todolistapplication.enumerations.Status;
import ru.uwurahara.todolistapplication.model.Task;
import ru.uwurahara.todolistapplication.util.StatusSortUtil;
import ru.uwurahara.todolistapplication.repository.TaskRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.uwurahara.todolistapplication.mapper.TaskMapper.mapToTaskResponseDto;

@Service
@Validated
public class TaskServiceImpl implements TaskService {
    public final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) { this.taskRepository = taskRepository; }

    @Override
    @Transactional
    public TaskResponseDto create(TaskRequestDto taskRequestDto){
        Task task = new Task(taskRequestDto.getTitle(), taskRequestDto.getDescription(), taskRequestDto.getDeadline());
        task = taskRepository.save(task);

        return mapToTaskResponseDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto update(int id, TaskRequestDto updatedRecordData){

        Task task = taskRepository.findById(id).orElseThrow();

        task.setTitle(updatedRecordData.getTitle());
        task.setDescription(updatedRecordData.getDescription());
        task.setDeadline(updatedRecordData.getDeadline());
        task.setStatus(updatedRecordData.getStatus());

        task = taskRepository.save(task);

        return mapToTaskResponseDto(task);
    }

    @Override
    @Transactional
    public void delete(int id){
        taskRepository.findById(id).orElseThrow();
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<TaskResponseDto> findAll(Status filterByStatus, SortBy sortBy, SortDirection sortDirection){
        List<Task> tasks;

        if (filterByStatus != null){
            tasks = taskRepository.findByStatus(filterByStatus);
        } else {
            tasks = taskRepository.findAll();
        }

        List<TaskResponseDto> tasksDto = tasks.stream()
                .map(task -> new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.getDeadline(), task.getStatus()))
                .collect(Collectors.toList());

        if (sortBy == null){ return tasksDto; }
        switch (sortBy) {
            case STATUS:
                switch (sortDirection) {
                    case ASC:
                        tasksDto.sort(
                                Comparator.comparing(
                                        (TaskResponseDto o) -> StatusSortUtil.statusOrderAsc.get(o.getStatus())
                        ));
                        break;
                    case DESC:
                        tasksDto.sort(
                                Comparator.comparingInt(
                                        (TaskResponseDto o) -> StatusSortUtil.statusOrderAsc.get(o.getStatus()))
                                        .reversed()
                        );
                        break;
                }
                break;
            case DEADLINE:
                switch (sortDirection) {
                    case ASC:
                        tasksDto.sort(Comparator.comparing(TaskResponseDto::getDeadline));
                        break;
                    case DESC:
                        tasksDto.sort(Comparator.comparing(TaskResponseDto::getDeadline).reversed());
                        break;
                }
                break;
            default:
                break;
        }

        return tasksDto;
    }
}
