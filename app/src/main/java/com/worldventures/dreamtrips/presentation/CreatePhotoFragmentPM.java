package com.worldventures.dreamtrips.presentation;


import android.net.Uri;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.service.ServiceActionRunner;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.repository.Repository;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.utils.busevents.PhotoUploadFinished;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import timber.log.Timber;

public class CreatePhotoFragmentPM extends BasePresentation<CreatePhotoFragmentPM.View> {
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    @Inject
    @Global
    EventBus eventBus;
    private String date;
    private String time;

    public CreatePhotoFragmentPM(View view) {
        super(view);
    }

    public void onDataSet(int year, int month, int day) {
        SimpleDateFormat sim = new SimpleDateFormat(DATE_FORMAT);

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date date = calendar.getTime();
        String format = sim.format(date);
        view.setDate(format);
    }

    public void onTimeSet(int h, int m) {
        SimpleDateFormat sim = new SimpleDateFormat(TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, h);
        calendar.set(Calendar.MINUTE, m);
        Date date = calendar.getTime();
        String format = sim.format(date);
        view.setTime(format);
    }


    public void saveAction() {
        if (view.getImageUri().toString().isEmpty()) {
            view.informUser("Wrong image");
        } else {
            Repository<ImageUploadTask> repository = new Repository<ImageUploadTask>(Realm.getInstance(context), ImageUploadTask.class);
            ImageUploadTask ut = repository.create(new Repository.Consumer<ImageUploadTask>() {
                @Override
                public void consume(ImageUploadTask action) {
                    action.setFileUri(view.getImageUri().toString());
                    action.setTitle(view.getTitle());
                    //   action.setTags(getParsedText(view.getTags()));
                    action.setLatitude(0);
                    action.setLongitude(0);
                    action.setLocationName(view.getLocation());
                    action.setShotAt(getParsedDateTime(view.getDate(), view.getTime()));

                }
            });
            ImageUploadTask task = ImageUploadTask.copy(ut);
            DreamTripsRequest.UploadTripPhoto uploadTripPhoto = new DreamTripsRequest.UploadTripPhoto(task);
            view.inject(uploadTripPhoto);
            dreamSpiceManager.execute(uploadTripPhoto, new RequestListener<Photo>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {

                }

                @Override
                public void onRequestSuccess(Photo o) {

                }
            });
            view.end();
        }
    }

    private ArrayList<String> getParsedText(String tags) {
        String[] split = tags.split(",");
        String[] trimed = new String[split.length];
        for (int i = 0; i < trimed.length; i++)
            trimed[i] = split[i].trim();
        return new ArrayList<String>(Arrays.asList(trimed));
    }

    public Date getParsedDateTime(String dateS, String timeS) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(dateS);

            Date time = timeFormat.parse(timeS);
            return combineDateTime(date, time);
        } catch (ParseException e) {
            Timber.e(e, "");
        }
        return null;

    }

    private Date combineDateTime(Date date, Date time) {
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(time);

        calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
        calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
        calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
        calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));

        Date result = calendarA.getTime();
        return result;
    }

    public interface View extends BasePresentation.View {
        void end();

        public Uri getImageUri();

        String getLocation();

        String getTags();

        String getTitle();

        String getDate();

        String getTime();

        void setDate(String format);

        void setTime(String format);

        void inject(Object o);
    }
}
