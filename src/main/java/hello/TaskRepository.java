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
    /*TODO
    * set real kill
    * */
    public synchronized void kill(UUID uuid){
        Task task = repository.get(uuid);
        if(task.getScriptStatus() == ScriptStatus.RUNNING) {
            task.setScriptStatus(ScriptStatus.KILLED);
        }
    }

    public synchronized void killAll(){
        for (Task task : loadAll()) {
            kill(task.getId());
        }
    }

    public synchronized void delete(UUID uuid){
        repository.remove(uuid);
    }

    public synchronized void deleteAll(){
        repository.clear();
    }
}
