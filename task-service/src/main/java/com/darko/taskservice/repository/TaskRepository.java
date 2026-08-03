package com.darko.taskservice.repository;

import com.darko.taskservice.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findAll();

    void deleteById(Long id);
}