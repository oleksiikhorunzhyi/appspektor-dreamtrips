package com.worldventures.dreamtrips.modules.video;

import android.content.Intent;
import android.os.IBinder;

import com.techery.spares.service.InjectingIntentService;
import com.thin.downloadmanager.ThinDownloadManager;
import com.worldventures.dreamtrips.modules.video.model.DownloadVideoEntity;
import com.worldventures.dreamtrips.modules.video.request.DownloadVideoRequest;

import java.util.List;

import javax.inject.Inject;

public class DownloadVideoService extends InjectingIntentService {

    public static final String EXTRA_VIDEO = "EXTRA_VIDEO";

    @Inject
    ThinDownloadManager thinDownloadManager;

    public DownloadVideoService() {
        this("DownloadVideoService");
    }

    public DownloadVideoService(String name) {
        super(name);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        inject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DownloadVideoEntity entity = (DownloadVideoEntity) intent.getSerializableExtra(EXTRA_VIDEO);
        DownloadVideoRequest request = new DownloadVideoRequest(entity, this);
        thinDownloadManager.add(request);
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = super.getModules();
        modules.add(new VideoModule());
        return modules;
    }
}
