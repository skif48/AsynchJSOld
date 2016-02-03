package hello;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 * @deprecated it is now just wrapper around a map, consider removing this class completely
 */
@Repository
@Deprecated
public class TaskRepository {

    private final Map<UUID, Task> repository = new ConcurrentHashMap<UUID, Task>();

    public void store(Task task){
        repository.put(task.getId(), task);
    }

    public Optional<Task> load(UUID id){
        return Optional.ofNullable(repository.get(id));
    }

    public Collection<Task> loadAll() {
        return new ArrayList<Task>(repository.values());
    }

    public void setKilled(UUID uuid){
        load(uuid).get().kill();
    }

    public void setAllKilled(){
        for (Task task : repository.values()) {
            task.kill();
        }
    }

    public Task delete(UUID uuid){
        return repository.remove(uuid);
    }

    public synchronized void deleteAllCompletedTasks(){
    	// TODO only delete completed/error/killed
        repository.clear();
    }
}
