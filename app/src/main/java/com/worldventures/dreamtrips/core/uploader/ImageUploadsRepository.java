package com.worldventures.dreamtrips.core.uploader;

import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;

import java.util.UUID;

import io.realm.Realm;

public class ImageUploadsRepository extends Repository<ImageUploadTask> {

    public ImageUploadsRepository(Realm realm) {
        super(realm, ImageUploadTask.class);
    }

    @Override
    public ImageUploadTask create(Consumer<ImageUploadTask> consumer) {
        return super.create((item) -> {
            item.setTaskId(UUID.randomUUID().toString());
            consumer.consume(item);
        });
    }
}
