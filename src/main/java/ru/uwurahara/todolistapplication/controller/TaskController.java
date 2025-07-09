package ru.uwurahara.todolistapplication.controller;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.uwurahara.todolistapplication.dto.TaskRequestDto;
import ru.uwurahara.todolistapplication.enumerations.SortBy;
import ru.uwurahara.todolistapplication.enumerations.SortDirection;
import ru.uwurahara.todolistapplication.enumerations.Status;
import ru.uwurahara.todolistapplication.service.TaskService;

@RestController
@RequestMapping("/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid TaskRequestDto task){
        return ResponseEntity.ok(taskService.create(task));
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody @Valid TaskRequestDto task, @RequestParam int id){
        return ResponseEntity.ok(taskService.update(id, task));
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestParam int id){
        taskService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam Status filterByStatus,
                                  @RequestParam SortBy sortBy,
                                  @RequestParam SortDirection sortDirection){

        return ResponseEntity.ok(taskService.findAll(filterByStatus, sortBy, sortDirection));
    }
}