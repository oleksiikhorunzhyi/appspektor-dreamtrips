package com.worldventures.dreamtrips.modules.bucketlist.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketPhoto;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class FindBucketItemByPhotoCommand extends Command<BucketItem> implements InjectableAction {
    @Inject
    BucketInteractor bucketInteractor;

    private BucketPhoto photo;

    public FindBucketItemByPhotoCommand(BucketPhoto photo) {
        this.photo = photo;
    }

    @Override
    protected void run(CommandCallback<BucketItem> callback) throws Throwable {
        bucketInteractor.bucketListActionPipe().observeSuccessWithReplay()
                .map(BucketListCommand::getResult)
                .flatMap(Observable::from)
                .filter(item -> item.getPhotos().contains(photo))
                .first()
                .timeout(1, TimeUnit.SECONDS)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}