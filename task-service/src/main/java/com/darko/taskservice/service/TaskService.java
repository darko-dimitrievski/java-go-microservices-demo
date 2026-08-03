package com.darko.taskservice.service;

import com.darko.taskservice.dto.CreateTaskRequest;
import com.darko.taskservice.dto.NotificationRequest;
import com.darko.taskservice.model.Task;
import com.darko.taskservice.model.TaskStatus;
import com.darko.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NotificationClient notificationClient;

    public TaskService(TaskRepository taskRepository, NotificationClient notificationClient) {
        this.taskRepository = taskRepository;
        this.notificationClient = notificationClient;
    }

    public Task createTask(CreateTaskRequest request) {
        Instant now = Instant.now();
        Task task = new Task(null, request.title(), request.description(), TaskStatus.TODO, now, now);
        Task saved = taskRepository.save(task);

        notificationClient.notify(new NotificationRequest(
                "TASK_CREATED",
                saved.getId(),
                saved.getTitle(),
                "New task created: " + saved.getTitle()
        ));

        return saved;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found: " + id));
    }

    public Task completeTask(Long id) {
        Task task = getTask(id);
        task.setStatus(TaskStatus.DONE);
        task.setUpdatedAt(Instant.now());
        Task saved = taskRepository.save(task);

        notificationClient.notify(new NotificationRequest(
                "TASK_COMPLETED",
                saved.getId(),
                saved.getTitle(),
                "Task completed: " + saved.getTitle()
        ));

        return saved;
    }

    public void deleteTask(Long id) {
        getTask(id); // 404 if missing
        taskRepository.deleteById(id);
    }
}