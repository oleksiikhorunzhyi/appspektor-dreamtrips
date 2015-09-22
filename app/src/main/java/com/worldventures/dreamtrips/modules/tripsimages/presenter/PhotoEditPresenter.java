package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.api.EditTripPhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import icepick.Icicle;

public class PhotoEditPresenter extends Presenter<PhotoEditPresenter.View> {

    @Icicle
    Photo photo;

    public PhotoEditPresenter(EditPhotoBundle editPhotoBundle) {
        photo = editPhotoBundle.getPhoto();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        syncUi();
    }

    private void syncUi() {

        Date date = photo.getShotAt() != null ? photo.getShotAt() : Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        onDataSet(year, month, day);
        onTimeSet(hour, minute);

        view.setImage(Uri.parse(photo.getFSImage().getUrl()));
        view.setTitle(photo.getTitle());
        view.setLocation(photo.getFsLocation());

        if (photo.getTags() != null)
            view.setTags(TextUtils.join(", ", photo.getTags()));
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
        if (view != null) {
            UploadTask imageUploadTask = new UploadTask();
            imageUploadTask.setTitle(view.getTitle());

            List<String> tags = Queryable.from(view.getTags().split(",")).map(String::trim).toList();
            imageUploadTask.setTags(new ArrayList<>(tags));
            imageUploadTask.setLatitude(0);
            imageUploadTask.setLongitude(0);
            imageUploadTask.setLocationName(view.getLocation());

            Date date = DateTimeUtils.dateFromString(view.getDate());
            Date time = DateTimeUtils.timeFromString(view.getTime());
            imageUploadTask.setShotAt(DateTimeUtils.mergeDateTime(date, time));

            doRequest(new EditTripPhotoCommand(photo.getUid(), imageUploadTask), updatedPhoto -> {
                eventBus.post(new FeedEntityChangedEvent((updatedPhoto)));
                view.finish();
                view = null;
            });
        }
    }

    public interface View extends Presenter.View {

        void finish();

        void setImage(Uri uri);

        void setTitle(String title);

        void setLocation(String location);

        void setTags(String tags);

        String getLocation();

        String getTags();

        String getTitle();

        String getDate();

        void setDate(String format);

        String getTime();

        void setTime(String format);

    }
}
