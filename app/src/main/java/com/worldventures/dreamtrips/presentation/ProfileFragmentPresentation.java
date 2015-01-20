package com.worldventures.dreamtrips.presentation;

import android.net.Uri;

import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.utils.busevents.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.view.dialog.ImagePickCallback;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

@PresentationModel
public class ProfileFragmentPresentation extends BasePresentation<ProfileFragmentPresentation.View> implements HasPresentationModelChangeSupport {

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
    @Global
    protected EventBus eventBus;

    @Inject
    DreamTripsApi dreamTripsApi;

    private ImagePickCallback avatarCallback = (image, error) -> {
        final File file = new File(image.getFileThumbnail());
        final TypedFile typedFile = new TypedFile("image/*", file);
        dreamTripsApi.uploadAvatar(typedFile, new Callback<User.Avatar>() {
            @Override
            public void success(User.Avatar avatar, Response response) {
                UserSession userSession = appSessionHolder.get().get();
                User user = userSession.getUser();

                user.setAvatar(avatar);

                ProfileFragmentPresentation.this.appSessionHolder.put(userSession);

                eventBus.post(new UpdateUserInfoEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                handleError(error);
            }
        });

        view.setAvatarImage(Uri.fromFile(new File(image.getFileThumbnail())));
    };

    private ImagePickCallback coverCallback = (image, error) -> {
        view.setCoverImage(Uri.fromFile(new File(image.getFileThumbnail())));

        UserSession userSession = this.appSessionHolder.get().get();
        User user = userSession.getUser();

        user.setCoverPath(image.getFileThumbnail());

        this.appSessionHolder.put(userSession);

        eventBus.post(new UpdateUserInfoEvent());
    };

    public ProfileFragmentPresentation(View view) {
        super(view);
        this.view = view;
        this.changeSupport = new PresentationModelChangeSupport(this);
    }

    @Override
    public void resume() {
        super.resume();

        User user = this.appSessionHolder.get().get().getUser();
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

        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.setCoverImage(Uri.fromFile(new File(user.getCoverPath())));
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
        this.appSessionHolder.destroy();
        activityRouter.finish();
        activityRouter.openLogin();
    }

    public void onDataSet(int year, int month, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar calendar = new GregorianCalendar(year, month, day);
        setDateOfBirth(sdf.format(calendar.getTime()));
        changeSupport.firePropertyChange(DATE_OF_BIRTH);
    }

    //don't use of get prefix
    public ImagePickCallback provideAvatarChooseCallback() {
        return avatarCallback;
    }

    public ImagePickCallback provideCoverChooseCallback() {
        return coverCallback;
    }


    public static interface View extends BasePresentation.View {
        public void setAvatarImage(Uri uri);

        public void setCoverImage(Uri uri);
    }
}
