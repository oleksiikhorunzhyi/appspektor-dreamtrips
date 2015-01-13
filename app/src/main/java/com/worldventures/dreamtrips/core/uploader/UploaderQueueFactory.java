package com.worldventures.dreamtrips.core.uploader;

import android.content.Context;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.JobQueue;
import com.path.android.jobqueue.QueueFactory;

public class UploaderQueueFactory implements QueueFactory {

    private final QueueFactory queueFactory;
    private JobQueue persistentQueue;
    private JobQueue nonPersistentQueue;

    public UploaderQueueFactory() {
        this.queueFactory = new JobManager.DefaultQueueFactory();
    }

    @Override
    public JobQueue createPersistentQueue(Context context, Long sessionId, String id) {
        this.persistentQueue = this.queueFactory.createPersistentQueue(context, sessionId, id);
        return this.persistentQueue;
    }

    @Override
    public JobQueue createNonPersistent(Context context, Long sessionId, String id) {
        this.nonPersistentQueue = this.queueFactory.createNonPersistent(context, sessionId, id);
        return this.nonPersistentQueue;
    }
}