package com.worldventures.dreamtrips.view.presentation;

import android.net.Uri;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.utils.busevents.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.view.dialog.PickImageDialog;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

@PresentationModel
public class ProfileFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {

    public static final String FROM = "from";
    public static final String LIVE_IN = "livesIn";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String USER_ID = "userId";
    public static final String USER_NOTE = "userName";
    public static final String USER_EMAIL = "userEmail";

    private final PresentationModelChangeSupport changeSupport;
    protected View view;
    protected String from;
    protected String livesIn;
    protected String dateOfBirth;
    protected String userId;
    protected String userName;
    protected String userEmail;

    @Inject
    protected EventBus eventBus;

    private PickImageDialog.Callback avatarCallback = (image, error) -> {
        dataManager.uploadAvatar(
                new File(image.getFileThumbnail()),
                (avatar, e) -> {
                    if (e == null) {
                        sessionManager.getCurrentUser().setAvatar(avatar);
                        eventBus.post(new UpdateUserInfoEvent());
                    } else {
                        handleError(e);
                    }
                });
        view.setAvatarImage(Uri.fromFile(new File(image.getFileThumbnail())));
    };

    private PickImageDialog.Callback coverCallback = (image, error) -> {
        view.setCoverImage(Uri.fromFile(new File(image.getFileThumbnail())));
        sessionManager.getCurrentUser().setCoverPath(image.getFileThumbnail());
        eventBus.post(new UpdateUserInfoEvent());
    };

    public ProfileFragmentPresentation(View view, Injector injector) {
        super(view, injector);
        this.view = view;
        this.changeSupport = new PresentationModelChangeSupport(this);
        User user = sessionManager.getCurrentUser();
        setUserName(user.getUsername());
        setUserEmail(user.getEmail());
        setUserId(user.getUsername());
        setLivesIn(user.getLocation());
        setFrom(user.getLocation());
        changeSupport.firePropertyChange(FROM);
        changeSupport.firePropertyChange(LIVE_IN);
        changeSupport.firePropertyChange(DATE_OF_BIRTH);
        changeSupport.firePropertyChange(USER_ID);
        changeSupport.firePropertyChange(USER_NOTE);
        changeSupport.firePropertyChange(USER_EMAIL);
    }


    public void onViewCreated() {
        User currentUser = sessionManager.getCurrentUser();
        view.setAvatarImage(currentUser.getAvatar().getMediumUri());
        view.setCoverImage(Uri.fromFile(new File(currentUser.getCoverPath())));

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLivesIn() {
        return livesIn;
    }

    public void setLivesIn(String livesIn) {
        this.livesIn = livesIn;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void logout() {
        sessionManager.logoutUser();
        activityRouter.openLogin();
        activityRouter.finish();
    }

    public void onDataSet(int year, int month, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar calendar = new GregorianCalendar(year, month, day);
        setDateOfBirth(sdf.format(calendar.getTime()));
        changeSupport.firePropertyChange(DATE_OF_BIRTH);
    }

    //don't use of getAvata...
    public PickImageDialog.Callback provideAvatarChooseCallback() {
        return avatarCallback;
    }

    public PickImageDialog.Callback provideCoverChooseCallback() {
        return coverCallback;
    }


    public static interface View extends IInformView {
        public void setAvatarImage(Uri uri);

        public void setCoverImage(Uri uri);
    }
}
