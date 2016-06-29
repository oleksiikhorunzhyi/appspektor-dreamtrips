package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import android.content.Context;
import android.support.v4.util.Pair;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.api.uploadery.UploaderyManager;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UploadPhotoHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.subjects.PublishSubject;

import static com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder.State.DONE;
import static com.worldventures.dreamtrips.modules.bucketlist.service.model.EntityStateHolder.State.FAIL;

@CommandAction
public class AddBucketItemPhotoCommand extends Command<Pair<BucketItem, BucketPhoto>> implements InjectableAction {
    @Inject
    @ForApplication
    Context context;

    @Inject
    Janet janet;

    @Inject
    BucketInteractor bucketInteractor;

    @Inject
    UploaderyManager uploaderyManager;

    private PublishSubject<Object> cancelSubject = PublishSubject.create();

    private BucketItem bucketItem;
    private EntityStateHolder<BucketPhoto> photoEntityStateHolder;

    public AddBucketItemPhotoCommand(BucketItem bucketItem, String path) {
        this.bucketItem = bucketItem;
        this.photoEntityStateHolder = EntityStateHolder.create(createLocalBucketPhoto(path),
                EntityStateHolder.State.PROGRESS);
    }

    @Override
    protected void run(CommandCallback<Pair<BucketItem, BucketPhoto>> callback) throws Throwable {
        janet.createPipe(CopyFileCommand.class)
                .createObservableResult(new CopyFileCommand(context, photoEntityStateHolder.entity().getImagePath()))
                .map(Command::getResult)
                .flatMap(path -> uploaderyManager.getUploadImagePipe()
                        .createObservableResult(new SimpleUploaderyCommand(path, path.hashCode())))
                .cast(SimpleUploaderyCommand.class)
                .map(uploaderyCommand -> uploaderyCommand.getResult().getPhotoUploadResponse().getLocation())
                .map(location -> {
                    BucketPhoto bucketPhoto = new BucketPhoto();
                    bucketPhoto.setOriginUrl(location);

                    return bucketPhoto;
                })
                .flatMap(photo -> janet.createPipe(UploadPhotoHttpAction.class)
                        .createObservableResult(new UploadPhotoHttpAction(bucketItem.getUid(), photo))
                        .map(UploadPhotoHttpAction::getResponse))
                .map(photo -> {
                    if (bucketItem.getCoverPhoto() == null) {
                        bucketItem.setCoverPhoto(photo);
                    }

                    bucketItem.getPhotos().add(0, photo);
                    return Pair.create(bucketItem, photo);
                })
                .takeUntil(cancelSubject
                        .doOnNext(object -> bucketInteractor.uploadControllerCommandPipe()
                                .send(UploadPhotoControllerCommand.cancel(bucketItem.getUid(), photoEntityStateHolder))))
                .doOnNext(bucketItemBucketPhotoPair -> {
                    photoEntityStateHolder.setEntity(bucketItemBucketPhotoPair.second);
                    photoEntityStateHolder.setState(DONE);
                })
                .doOnError(throwable -> photoEntityStateHolder.setState(FAIL))
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    protected void cancel() {
        cancelSubject.onNext(null);
    }

    public EntityStateHolder<BucketPhoto> photoEntityStateHolder() {
        return photoEntityStateHolder;
    }

    public String bucketUid() {
        return bucketItem.getUid();
    }

    /////////////////////////
    // Common
    /////////////////////////
    private BucketPhoto createLocalBucketPhoto(String path) {
        BucketPhoto photo = new BucketPhoto();
        photo.setUrl(path);

        return photo;
    }
}