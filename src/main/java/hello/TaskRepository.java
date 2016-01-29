package hello;

import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Created by Vladyslav Usenko on 16.01.2016.
 */
@Repository
public class TaskRepository {

    private final Map<UUID, Task> repository;

    public TaskRepository() {
        this.repository = new HashMap<UUID, Task>();
    }

    public synchronized void store(Task task){
        repository.put(task.getId(), task);
    }

    public synchronized Task load(UUID id){
        return new Task(repository.get(id));
    }

    public synchronized Collection<Task> loadAll() {
        return new ArrayList<Task>(repository.values());
    }

    public synchronized void setKilled(UUID uuid){
        Task task = repository.get(uuid);
        switch (task.getScriptStatus()){
            case WAITING:
                task.setScriptStatus(ScriptStatus.KILLED);
                break;
            case RUNNING:
                task.setScriptStatus(ScriptStatus.KILLED);
                break;
            default: break;
        }
    }

    public synchronized void setAllKilled(){
        for (Task task : loadAll()) {
            setKilled(task.getId());
        }
    }

    public synchronized void delete(UUID uuid){
        repository.remove(uuid);
    }

    public synchronized void deleteAll(){
        repository.clear();
    }
}
