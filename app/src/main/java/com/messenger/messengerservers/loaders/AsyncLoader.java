package com.messenger.messengerservers.loaders;

import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class AsyncLoader<T> extends Loader<T> {

    private ExecuteTask executeTask = new ExecuteTask();
    private final ExecutorService executorService;

    protected AsyncLoader(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void load() {
        if (executorService != null) {
            executorService.execute(executeTask);
        } else {
            execute();
        }
    }

    protected abstract List<T> loadEntities();

    private void execute() {
        List<T> entities = loadEntities();
        if (persister != null)
            persister.save(entities);
        if (onEntityLoadedListener != null)
            onEntityLoadedListener.onLoaded(entities);
    }

    private class ExecuteTask implements Runnable {
        @Override
        public void run() {
            execute();
        }
    }
}

