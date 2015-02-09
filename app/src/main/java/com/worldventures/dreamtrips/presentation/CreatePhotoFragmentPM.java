package com.worldventures.dreamtrips.presentation;


import android.net.Uri;

import com.techery.spares.service.ServiceActionRunner;
import com.worldventures.dreamtrips.core.uploader.UploadingService;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import timber.log.Timber;

@PresentationModel
public class CreatePhotoFragmentPM extends BasePresentation<CreatePhotoFragmentPM.View> implements HasPresentationModelChangeSupport {
    public static final String TIME = "time";
    public static final String DATE = "date";
    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String TIME_FORMAT = "hh:mm a";
    private final PresentationModelChangeSupport changeSupport;
    @Inject
    ServiceActionRunner serviceActionRunner;

    String title;
    String location;
    String date;
    String time;
    String tags;
    private Uri imageUri;

    public CreatePhotoFragmentPM(View view) {
        super(view);
        this.changeSupport = new PresentationModelChangeSupport(this);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        changeSupport.firePropertyChange("title");
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        changeSupport.firePropertyChange("location");
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        changeSupport.firePropertyChange("date");
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        changeSupport.firePropertyChange("time");
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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
        setDate(format);
    }

    public void onTimeSet(int h, int m) {
        SimpleDateFormat sim = new SimpleDateFormat(TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR, h);
        calendar.set(Calendar.MINUTE, m);
        Date date = calendar.getTime();
        String format = sim.format(date);
        setTime(format);
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }

    public void saveAction() {
        if (imageUri.toString().isEmpty()) {
            view.informUser("Wrong image");
        } else {
            UploadingService.ImageUploadAction action = new UploadingService.ImageUploadAction();
            action.setFileUri(imageUri.toString());
            action.setTitle(getTitle());
            action.setTags(getParsedText(getTags()));
            action.setLatitude(56);
            action.setLongitude(92);
            action.setLocationName("Krasnoyarsk");
            action.setShotAt(getParsedDateTime(getDate(), getTime()));
            serviceActionRunner.from(UploadingService.class).run(action);
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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }


    public interface View extends BasePresentation.View {
        void end();
    }
}