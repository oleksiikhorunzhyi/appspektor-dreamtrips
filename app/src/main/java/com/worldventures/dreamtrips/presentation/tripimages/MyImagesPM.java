package com.worldventures.dreamtrips.presentation.tripimages;

import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;

import org.robobinding.annotation.PresentationModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

@PresentationModel
public class MyImagesPM extends TripImagesListPM<Object> {
    public MyImagesPM(View view) {
        super(view, Type.MY_IMAGES);
    }

    @Override
    public void loadPhotos(int perPage, int page, Callback<List<Object>> callback) {
        User user = appSessionHolder.get().get().getUser();
        dreamTripsApi.getMyPhotos(user.getId(), perPage, page, new Callback<List<Photo>>() {
            @Override
            public void success(List<Photo> photos, Response response) {
                List<ImageUploadTask> uploadTasks = getUploadTasks();
                ArrayList<Object> result = new ArrayList<>();
                result.addAll(uploadTasks);
                result.addAll(photos);
                callback.success(result, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
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