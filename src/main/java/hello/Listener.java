package hello;

import java.util.concurrent.Future;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public interface Listener {
    void onStart(Task task, Future<String> future);
    void onComplete(Task task);
}
