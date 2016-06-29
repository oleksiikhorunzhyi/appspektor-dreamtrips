package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class MergeBucketItemPhotosWithStorageCommand extends Command<List<EntityStateHolder<BucketPhoto>>>
        implements InjectableAction {
    @Inject
    BucketInteractor bucketInteractor;

    private String bucketUid;

    private List<BucketPhoto> listOfBucketPhoto;

    public MergeBucketItemPhotosWithStorageCommand(String bucketUid, List<BucketPhoto> listOfBucketPhoto) {
        this.bucketUid = bucketUid;
        this.listOfBucketPhoto = listOfBucketPhoto;
    }

    @Override
    protected void run(CommandCallback<List<EntityStateHolder<BucketPhoto>>> callback) throws Throwable {
        Observable.zip(photoAsDoneObservable(), bucketInteractor.uploadControllerCommandPipe()
                        .createObservableResult(UploadPhotoControllerCommand.fetch(bucketUid))
                        .map(UploadPhotoControllerCommand::getResult),
                (existedPhotos, undonePhotos) -> {
                    existedPhotos.addAll(undonePhotos);
                    return existedPhotos;
                })
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private Observable<List<EntityStateHolder<BucketPhoto>>> photoAsDoneObservable() {
        return Observable.just(convertListOfPhotos());
    }

    private List<EntityStateHolder<BucketPhoto>> convertListOfPhotos() {
        List<EntityStateHolder<BucketPhoto>> photoStateList = new ArrayList<>(listOfBucketPhoto.size());
        for (BucketPhoto photo : listOfBucketPhoto) {
            photoStateList.add(EntityStateHolder.create(photo, EntityStateHolder.State.DONE));
        }

        return photoStateList;
    }
}