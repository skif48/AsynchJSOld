package hello;

import java.util.UUID;
import java.util.concurrent.Future;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public interface Listener {
    void onStart(UUID id, Future<TransferData> future);
    void onComplete(Task task);
}
