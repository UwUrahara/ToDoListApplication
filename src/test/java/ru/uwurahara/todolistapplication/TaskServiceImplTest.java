package ru.uwurahara.todolistapplication;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.uwurahara.todolistapplication.dto.TaskRequestDto;
import ru.uwurahara.todolistapplication.dto.TaskResponseDto;
import ru.uwurahara.todolistapplication.model.Task;
import ru.uwurahara.todolistapplication.repository.TaskRepository;
import ru.uwurahara.todolistapplication.service.TaskServiceImpl;
import ru.uwurahara.todolistapplication.enumerations.SortBy;
import ru.uwurahara.todolistapplication.enumerations.SortDirection;
import ru.uwurahara.todolistapplication.enumerations.Status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;

    @Test
    void create_shouldSuccessfullyCreateTask() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(1);
        TaskRequestDto requestDto = new TaskRequestDto(
                "Valid title",
                "Valid description",
                futureDate,
                Status.TODO
        );

        Task savedTask = new Task(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getDeadline()
        );
        savedTask.setStatus(requestDto.getStatus());

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        TaskResponseDto result = taskService.create(requestDto);

        // Then
        assertNotNull(result);
        assertEquals(requestDto.getTitle(), result.getTitle());
        assertEquals(requestDto.getDescription(), result.getDescription());
        assertEquals(requestDto.getDeadline(), result.getDeadline());
        assertEquals(requestDto.getStatus(), result.getStatus());

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void create_shouldThrowExceptionWhenTitleIsNull() {
        // Given
        TaskRequestDto invalidRequest = new TaskRequestDto(
                null,
                "Description",
                LocalDate.now().plusDays(1),
                Status.TODO
        );

        // When & Then
        assertThrows(
                ConstraintViolationException.class,
                () -> taskService.create(invalidRequest)
        );
        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowExceptionWhenTitleIsBlank() {
        // Given
        TaskRequestDto invalidRequest = new TaskRequestDto(
                "   ",
                "Description",
                LocalDate.now().plusDays(1),
                Status.TODO
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.create(invalidRequest)
        );
        assertEquals("Название задачи не может быть пустым", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowExceptionWhenDeadlineIsNull() {
        // Given
        TaskRequestDto invalidRequest = new TaskRequestDto(
                "Title",
                "Description",
                null,
                Status.TODO
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.create(invalidRequest)
        );
        assertEquals("Дедлайн задачи не может быть пустым", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowExceptionWhenDeadlineIsPast() {
        // Given
        TaskRequestDto invalidRequest = new TaskRequestDto(
                "Title",
                "Description",
                LocalDate.now().minusDays(1),
                Status.TODO
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.create(invalidRequest)
        );
        assertEquals("Задача не может быть создана с дедлайном ранее сегодняшней даты", exception.getMessage());
        verify(taskRepository, never()).save(any());
    }

    @Test
    void create_shouldSetDefaultStatusWhenNotProvided() {
        // Given
        LocalDate futureDate = LocalDate.now().plusDays(1);
        TaskRequestDto requestDto = new TaskRequestDto(
                "Valid title",
                "Valid description",
                futureDate,
                null  // Status not provided
        );

        Task savedTask = new Task(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getDeadline()
        );
        savedTask.setStatus(Status.TODO);  // Assuming default is NEW

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // When
        TaskResponseDto result = taskService.create(requestDto);

        // Then
        assertNotNull(result);
        assertEquals(Status.TODO, result.getStatus());
    }

// ---------------------------------------------------------------------------------------------------------------------
    @Test
    void update_shouldSuccessfullyUpdateTask() {
        // Given
        int taskId = 1;
        LocalDate futureDate = LocalDate.now().plusDays(1);
        TaskRequestDto requestDto = new TaskRequestDto(
                "Updated title",
                "Updated description",
                futureDate,
                Status.IN_PROGRESS
        );

        Task existingTask = new Task();
        existingTask.setTitle("Old title");
        existingTask.setDescription("Old description");
        existingTask.setDeadline(LocalDate.now().plusDays(2));
        existingTask.setStatus(Status.TODO);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TaskResponseDto result = taskService.update(taskId, requestDto);

        // Then
        assertNotNull(result);
        assertEquals(requestDto.getTitle(), result.getTitle());
        assertEquals(requestDto.getDescription(), result.getDescription());
        assertEquals(requestDto.getDeadline(), result.getDeadline());
        assertEquals(requestDto.getStatus(), result.getStatus());

        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void update_shouldThrowExceptionWhenTaskNotFound() {
        // Given
        int nonExistentId = 99;
        TaskRequestDto requestDto = new TaskRequestDto(
                "Title",
                "Description",
                LocalDate.now().plusDays(1),
                Status.TODO
        );

        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoSuchElementException.class, () -> taskService.update(nonExistentId, requestDto));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void update_shouldValidateTitleNotEmpty() {
        // Given
        TaskRequestDto invalidRequest = new TaskRequestDto(
                null,  // Пустой title
                "Description",
                LocalDate.now().plusDays(1),
                Status.TODO
        );
        Task task = new Task(invalidRequest.getTitle(), invalidRequest.getDescription(), invalidRequest.getDeadline());
        task.setStatus(Status.TODO);
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.update(1, invalidRequest)
        );
        assertEquals("Название задачи не может быть пустым", exception.getMessage());
    }

    @Test
    void update_shouldValidateDeadlineNotNull() {
        // Given
        TaskRequestDto invalidRequest = new TaskRequestDto(
                "Title",
                "Description",
                null,  // Пустой deadline
                Status.TODO
        );
        Task task = new Task(invalidRequest.getTitle(), invalidRequest.getDescription(), invalidRequest.getDeadline());
        task.setStatus(Status.TODO);
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.update(1, invalidRequest)
        );
        assertEquals("Дедлайн задачи не может быть пустым", exception.getMessage());
    }

    @Test
    void update_shouldValidateDeadlineNotInPast() {
        // Given
        TaskRequestDto invalidRequest = new TaskRequestDto(
                "Title",
                "Description",
                LocalDate.now().minusDays(1),  // Прошедшая дата
                Status.TODO
        );
        Task task = new Task(invalidRequest.getTitle(), invalidRequest.getDescription(), invalidRequest.getDeadline());
        task.setStatus(Status.TODO);
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> taskService.update(1, invalidRequest)
        );
        assertEquals("Задача не может быть создана с дедлайном ранее сегодняшней даты", exception.getMessage());
    }

// ---------------------------------------------------------------------------------------------------------------------
    @Test
    void delete_shouldDeleteTaskWhenExists() {
        // Given
        int taskId = 1;
        Task mockTask = new Task();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
        doNothing().when(taskRepository).deleteById(taskId);

        // When
        assertDoesNotThrow(() -> taskService.delete(taskId));

        // Then
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void delete_shouldThrowExceptionWhenTaskNotFound() {
        // Given
        int nonExistentId = 99;
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        assertThrows(NoSuchElementException.class, () -> taskService.delete(nonExistentId));

        // Then
        verify(taskRepository, times(1)).findById(nonExistentId);
        verify(taskRepository, never()).deleteById(anyInt());
    }

// ---------------------------------------------------------------------------------------------------------------------
    private final Task task1 = new Task("Task A", "Desc A", LocalDate.now().plusDays(3));
    private final Task task2 = new Task("Task B", "Desc B", LocalDate.now().plusDays(1));
    private final Task task3 = new Task("Task C", "Desc C", LocalDate.now().plusDays(2));

    @Test
    void findAll_shouldReturnAllTasksWhenNoFilter() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3));

        // When
        List<TaskResponseDto> result = taskService.findAll(null, null, null);

        // Then
        assertEquals(3, result.size());
        verify(taskRepository, times(1)).findAll();
        verify(taskRepository, never()).findByStatus(any());
    }

    @Test
    void findAll_shouldFilterByStatus() {
        // Given
        task1.setStatus(Status.TODO);
        task2.setStatus(Status.IN_PROGRESS);
        task3.setStatus(Status.DONE);
        when(taskRepository.findByStatus(Status.IN_PROGRESS))
                .thenReturn(List.of(task1));

        // When
        List<TaskResponseDto> result = taskService.findAll(Status.IN_PROGRESS, null, null);

        // Then
        assertEquals(1, result.size());
        assertEquals("Task A", result.getFirst().getTitle());
        verify(taskRepository, times(1)).findByStatus(Status.IN_PROGRESS);
    }

    @Test
    void findAll_shouldSortByStatusAsc() {
        // Given
        task1.setStatus(Status.TODO);
        task2.setStatus(Status.IN_PROGRESS);
        task3.setStatus(Status.DONE);
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3));

        // When
        List<TaskResponseDto> result = taskService.findAll(null, SortBy.STATUS, SortDirection.ASC);

        // Then
        assertEquals(3, result.size());
        assertEquals(Status.TODO, result.get(0).getStatus());    // 1st in order
        assertEquals(Status.IN_PROGRESS, result.get(1).getStatus()); // 2nd
        assertEquals(Status.DONE, result.get(2).getStatus());    // 3rd
    }

    @Test
    void findAll_shouldSortByDeadlineAsc() {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3));

        // When
        List<TaskResponseDto> result = taskService.findAll(null, SortBy.DEADLINE, SortDirection.ASC);

        // Then
        assertEquals(3, result.size());
        assertEquals("Task B", result.get(0).getTitle()); // Earliest deadline
        assertEquals("Task C", result.get(1).getTitle());
        assertEquals("Task A", result.get(2).getTitle()); // Latest deadline
    }

    @Test
    void findAll_shouldCombineFilterAndSort() {
        // Given
        task1.setStatus(Status.TODO);
        task2.setStatus(Status.IN_PROGRESS);
        task3.setStatus(Status.DONE);
        when(taskRepository.findByStatus(Status.DONE))
                .thenReturn(List.of(task3));

        // When
        List<TaskResponseDto> result = taskService.findAll(Status.DONE, SortBy.DEADLINE, SortDirection.DESC);

        // Then
        assertEquals(1, result.size());
        assertEquals("Task C", result.getFirst().getTitle());
        verify(taskRepository, times(1)).findByStatus(Status.DONE);
    }

    @Test
    void findAll_shouldReturnEmptyListWhenNoTasks() {
        // Given
        when(taskRepository.findAll()).thenReturn(List.of());

        // When
        List<TaskResponseDto> result = taskService.findAll(null, null, null);

        // Then
        assertTrue(result.isEmpty());
    }

}
