package com.worldventures.dreamtrips.presentation;

import android.content.Context;
import android.os.Handler;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

@PresentationModel
public class TripImagesListFragmentPresentation extends BasePresentation<TripImagesListFragmentPresentation.View> {

//    @Inject
//    Repository<ImageUploadTask> repository;


    @Inject
    DreamTripsApi dreamTripsApi;

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Context context;

    private TripImagesListFragment.Type type;
    private List<Object> photos;
    private CollectionController<Object> photosController;

    public TripImagesListFragmentPresentation(View view, TripImagesListFragment.Type type) {
        super(view);
        this.type = type;
    }

    public interface Command {
        List<ImageUploadTask> run();
    }

    public List<ImageUploadTask> runOnMainThread(Command command) {
        Handler mainHandler = new Handler(context.getMainLooper());
        final List<List<ImageUploadTask>> result = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                result.add(command.run());
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return result.get(0);
    }

    @Override
    public void init() {
        super.init();
        this.photosController = loaderFactory.create(this.type.ordinal(), (context, params) -> {

            this.photos = this.loadPhotos();
            ArrayList<Object> result = new ArrayList<>();
            result.addAll(photos);
            return result;
        });
    }

    private List<ImageUploadTask> loadUploadTasks() {
        Repository<ImageUploadTask> repository = new Repository<>(Realm.getInstance(context), ImageUploadTask.class);
        RealmResults<ImageUploadTask> all = repository.query().findAll();
        List<ImageUploadTask> list = Arrays.asList(all.toArray(new ImageUploadTask[all.size()]));
        Collections.reverse(list);
        return list;
    }

    public CollectionController<Object> getPhotosController() {
        return photosController;
    }

    public List<Object> loadPhotos() {
        switch (type) {
            case MY_IMAGES:
                final User user = appSessionHolder.get().get().getUser();
                List<Photo> myPhotos = dreamTripsApi.getMyPhotos(user.getId());
                List<ImageUploadTask> imageUploadTasks = runOnMainThread(this::loadUploadTasks);
                ArrayList<Object> result = new ArrayList<>();
                result.addAll(imageUploadTasks);
                result.addAll(myPhotos);
                return result;
            case MEMBER_IMAGES:
                return new ArrayList<>(dreamTripsApi.getUserPhotos());
            case YOU_SHOULD_BE_HERE:
                return new ArrayList<>(dreamTripsApi.getYouShoulBeHerePhotos());
            case INSPIRE_ME:
                return new ArrayList<>(dreamTripsApi.getInspirationsPhotos());

        }
        return new ArrayList<>();
    }

    public void onItemClick(int position) {
        if (photos.get(position) instanceof Photo) {
            this.activityRouter.openFullScreenPhoto(this.photos, position);
        }
    }


    public static interface View extends BasePresentation.View {
    }
}
