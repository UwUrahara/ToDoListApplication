package ru.uwurahara.todolistapplication.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.uwurahara.todolistapplication.dto.TaskRequestDto;
import ru.uwurahara.todolistapplication.dto.TaskResponseDto;
import ru.uwurahara.todolistapplication.enumerations.SortBy;
import ru.uwurahara.todolistapplication.enumerations.SortDirection;
import ru.uwurahara.todolistapplication.enumerations.Status;
import ru.uwurahara.todolistapplication.model.Task;
import ru.uwurahara.todolistapplication.repository.TaskRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService{
    public final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) { this.taskRepository = taskRepository; }

    @Override
    @Transactional
    public TaskResponseDto create(TaskRequestDto taskRequestDto){
        if (taskRequestDto.getTitle() == null || taskRequestDto.getTitle().isBlank()){
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }
        if (taskRequestDto.getDeadline() == null) {
            throw new IllegalArgumentException("Дедлайн задачи не может быть пустым");
        }
        if (taskRequestDto.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Задача не может быть создана с дедлайном ранее сегодняшней даты");
        }

        Task task = new Task(taskRequestDto.getTitle(), taskRequestDto.getDescription(), taskRequestDto.getDeadline());
        task = taskRepository.save(task);

        return new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.getDeadline(), task.getStatus());
    }

    @Override
    @Transactional
    public TaskResponseDto update(int id, TaskRequestDto updatedRecordData){

        Task task = taskRepository.findById(id).orElseThrow();

        if (updatedRecordData.getTitle() == null || updatedRecordData.getTitle().isBlank()){
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }
        if (updatedRecordData.getDeadline() == null) {
            throw new IllegalArgumentException("Дедлайн задачи не может быть пустым");
        }
        if (updatedRecordData.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Задача не может быть создана с дедлайном ранее сегодняшней даты");
        }

        task.setTitle(updatedRecordData.getTitle());
        task.setDescription(updatedRecordData.getDescription());
        task.setDeadline(updatedRecordData.getDeadline());
        task.setStatus(updatedRecordData.getStatus());

        task = taskRepository.save(task);

        return new TaskResponseDto(task.getId(), task.getTitle(), task.getDescription(), task.getDeadline(), task.getStatus());
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

        switch (sortBy) {
            case STATUS:
                switch (sortDirection) {
                    case ASC:
                        Map<Status, Integer> statusOrderAsc = Map.of(
                                Status.TODO, 1,
                                Status.IN_PROGRESS, 2,
                                Status.DONE, 3
                        );
                        tasksDto.sort(Comparator.comparing(o -> statusOrderAsc.get(o.getStatus())));
                        break;
                    case DESC:
                        Map<Status, Integer> statusOrderDesc = Map.of(
                                Status.TODO, 3,
                                Status.IN_PROGRESS, 2,
                                Status.DONE, 1
                        );
                        tasksDto.sort(Comparator.comparing(o -> statusOrderDesc.get(o.getStatus())));
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
