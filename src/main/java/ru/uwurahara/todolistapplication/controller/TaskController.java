package ru.uwurahara.todolistapplication.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.uwurahara.todolistapplication.dto.TaskRequestDto;
import ru.uwurahara.todolistapplication.enumerations.SortBy;
import ru.uwurahara.todolistapplication.enumerations.SortDirection;
import ru.uwurahara.todolistapplication.enumerations.Status;
import ru.uwurahara.todolistapplication.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody TaskRequestDto task){
        try {
            return ResponseEntity.ok(taskService.create(task));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody TaskRequestDto task, @RequestParam int id){
        try {
            return ResponseEntity.ok(taskService.update(id, task));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestParam int id){
        try {
            taskService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam Status filterByStatus,
                                  @RequestParam SortBy sortBy,
                                  @RequestParam SortDirection sortDirection){
        try {
            return ResponseEntity.ok(taskService.findAll(filterByStatus, sortBy, sortDirection));
        } catch (Exception e){
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }
}