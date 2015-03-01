package com.worldventures.dreamtrips.presentation.tripimages;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public class MyImagesPM extends TripImagesListPM<IFullScreenAvailableObject> {
    public MyImagesPM(View view) {
        super(view, Type.MY_IMAGES);
    }

    @Override
    public void loadPhotos(int perPage, int page, RequestListener<ArrayList<IFullScreenAvailableObject>> callback) {
        User user = appSessionHolder.get().get().getUser();

        dreamSpiceManager.execute(new DreamTripsRequest.GetMyPhotos(user.getId(), perPage, page), new RequestListener<ArrayList<Photo>>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                callback.onRequestFailure(spiceException);

            }

            @Override
            public void onRequestSuccess(ArrayList<Photo> photos) {
                List<ImageUploadTask> uploadTasks = getUploadTasks();
                ArrayList<IFullScreenAvailableObject> result = new ArrayList<>();
                result.addAll(ImageUploadTask.from(uploadTasks));
                result.addAll(photos);
                callback.onRequestSuccess(result);

            }
        });
    }

    private List<ImageUploadTask> getUploadTasks() {
        Repository<ImageUploadTask> repository = new Repository<>(Realm.getInstance(context), ImageUploadTask.class);
        RealmResults<ImageUploadTask> all = repository.query().findAll();
        List<ImageUploadTask> list = Arrays.asList(all.toArray(new ImageUploadTask[all.size()]));
        Collections.reverse(ImageUploadTask.copy(list));

        return list;
    }
}