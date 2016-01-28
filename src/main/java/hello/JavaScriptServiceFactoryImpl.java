package hello;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Vladyslav Usenko on 17.01.2016.
 */
public class JavaScriptServiceFactoryImpl implements JavaScriptServiceFactory {
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void createJavaScriptService(Task task, Listener listener) {
        executorService.submit(new JavaScriptService(task, listener, executorService));
    }
}
