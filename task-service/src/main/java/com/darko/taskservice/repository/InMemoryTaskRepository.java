package com.darko.taskservice.repository;

import com.darko.taskservice.model.Task;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple in-memory store so the demo runs with zero external infrastructure.
 * Swap for a JPA/Mongo repository if you want persistence.
 */
@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idSequence.incrementAndGet());
        }
        store.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Task> findAll() {
        return List.copyOf(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}