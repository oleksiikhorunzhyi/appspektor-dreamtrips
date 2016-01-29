package com.worldventures.dreamtrips.modules.tripsimages.presenter;


import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.InsertNewImageUploadTaskEvent;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoTag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class CreatePhotoPresenter extends Presenter<CreatePhotoPresenter.View> {

    @Inject
    protected SnappyRepository db;

    private String type;
    private boolean saved = false;

    public CreatePhotoPresenter(String type) {
        this.type = type;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        onDataSet(year, month, day);
        onTimeSet(hour, minute);
    }

    public void onDataSet(int year, int month, int day) {
        String format = DateTimeUtils.convertDateToString(year, month, day);
        view.setDate(format);
    }

    public void onTimeSet(int h, int m) {
        String format = DateTimeUtils.convertTimeToString(h, m);
        view.setTime(format);
    }

    public void saveAction() {
        if (!saved)
            if (view.getImageUri().toString().isEmpty()) {
                view.informUser(context.getString(R.string.wrong_image));
            } else {
                saved = true;
                UploadTask imageUploadTask = new UploadTask();
                imageUploadTask.setFilePath(view.getImageUri().toString());
                imageUploadTask.setTitle(view.getTitle());

                List<String> tags = Queryable.from(view.getTags().split(",")).map(String::trim).toList();
                imageUploadTask.setTags(new ArrayList<>(tags));
                imageUploadTask.setLatitude(0);
                imageUploadTask.setLongitude(0);
                imageUploadTask.setLocationName(view.getLocation());

                imageUploadTask.setModule(UploadTask.Module.IMAGES);

                Date date = DateTimeUtils.dateFromString(view.getDate());
                Date time = DateTimeUtils.timeFromString(view.getTime());
                imageUploadTask.setShotAt(DateTimeUtils.mergeDateTime(date, time));
                imageUploadTask.setType(type);

                doRequest(new CopyFileCommand(context, imageUploadTask.getFilePath()), filePath -> {
                    imageUploadTask.setFilePath(filePath);
                    imageUploadTask.setStatus(UploadTask.Status.IN_PROGRESS);
                    eventBus.post(new InsertNewImageUploadTaskEvent(imageUploadTask, view.getTagsToUpload()));
                    view.end();
                });
            }
    }

    public interface View extends Presenter.View {
        void end();

        Uri getImageUri();

        String getLocation();

        String getTags();

        String getTitle();

        String getDate();

        void setDate(String format);

        String getTime();

        void setTime(String format);

        void inject(Object o);

        List<PhotoTag> getTagsToUpload();
    }
}
