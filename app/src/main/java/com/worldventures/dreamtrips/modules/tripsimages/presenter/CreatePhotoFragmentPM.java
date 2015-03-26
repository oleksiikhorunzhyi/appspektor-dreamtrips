package com.worldventures.dreamtrips.modules.tripsimages.presenter;


import android.net.Uri;

import com.techery.spares.module.Annotations.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.events.InsertNewImageUploadTaskEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.DateTime;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class CreatePhotoFragmentPM extends Presenter<CreatePhotoFragmentPM.View> {
    @Inject
    @Global
    EventBus eventBus;

    @Inject
    SnappyRepository db;

    @Inject
    SessionHolder<UserSession> appSessionHolder;

    public CreatePhotoFragmentPM(View view) {
        super(view);
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
        if (view.getImageUri().toString().isEmpty()) {
            view.informUser("Wrong image");
        } else {

            ImageUploadTask action = new ImageUploadTask();
            action.setFileUri(view.getImageUri().toString());
            action.setTitle(view.getTitle());
            action.setUserName(appSessionHolder.get().get().getUser().getFullName());
            action.setTags(getParsedText(view.getTags()));
            action.setLatitude(0);
            action.setLongitude(0);
            action.setLocationName(view.getLocation());
            Date date = DateTimeUtils.dateFromString(view.getDate());
            Date time = DateTimeUtils.timeFromString(view.getTime());
            action.setShotAt(new DateTime(DateTimeUtils.mergeDateTime(date, time)));
            action.setTaskId(UUID.randomUUID().toString());
            db.saveUploadImageTask(action);

            eventBus.post(new InsertNewImageUploadTaskEvent(action));
            dreamSpiceManager.uploadPhoto(action);
            view.end();
        }
    }

    private ArrayList<String> getParsedText(String tags) {
        String[] split = tags.split(",");
        String[] trimed = new String[split.length];
        for (int i = 0; i < trimed.length; i++)
            trimed[i] = split[i].trim();
        return new ArrayList<>(Arrays.asList(trimed));
    }


    public interface View extends Presenter.View {
        void end();

        public Uri getImageUri();

        String getLocation();

        String getTags();

        String getTitle();

        String getDate();

        void setDate(String format);

        String getTime();

        void setTime(String format);

        void inject(Object o);
    }
}
