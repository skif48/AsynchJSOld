package hello;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Repository
public class TaskRepository {

    private final Map<UUID, Task> repository;

    public TaskRepository() {
        this.repository = new ConcurrentHashMap<UUID, Task>();
    }

    public void store(Task task){
        repository.put(task.getId(), task);
    }

    public Task load(UUID id){
        return repository.get(id);
    }

    public Collection<Task> loadAll() {
        return repository.values();
    }

    public void delete(UUID uuid){repository.remove(uuid);}
}
