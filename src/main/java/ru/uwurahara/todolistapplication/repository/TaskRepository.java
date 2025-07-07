package ru.uwurahara.todolistapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.uwurahara.todolistapplication.enumerations.Status;
import ru.uwurahara.todolistapplication.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByStatus(Status status);
}
