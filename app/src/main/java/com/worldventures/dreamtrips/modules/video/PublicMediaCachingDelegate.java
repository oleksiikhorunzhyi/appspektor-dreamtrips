package com.worldventures.dreamtrips.modules.video;

import android.content.Context;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.FileDownloadSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

public class PublicMediaCachingDelegate extends FileCachingDelegate{

    private String type;

    public PublicMediaCachingDelegate(SnappyRepository db, Context context, Injector injector, FileDownloadSpiceManager spiceManager, String environmentType) {
        super(db, context, injector, spiceManager);
        type = environmentType;
    }

    @Override
    public void downloadFile(CachedEntity entity) {
        String path = CachedEntity.getFileForStorage(type, entity.getUrl());
        startCaching(entity, path);
    }

    @Override
    public void deleteFile(CachedEntity entity){
        String path = CachedEntity.getFileForStorage(type, entity.getUrl());
        deleteFile(entity, path);
    }
}
