package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.profile.api.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import retrofit.mime.TypedFile;

public class ProfilePresenter extends Presenter<ProfilePresenter.View> {

    @Inject
    protected Prefs prefs;

    @Inject
    protected SnappyRepository snappyRepository;

    private ImagePickCallback avatarCallback = (fragment, image, error) -> {
        if (image != null) {
            final File file = new File(image.getFileThumbnail());
            final TypedFile typedFile = new TypedFile("image/*", file);
            view.avatarProgressVisible(true);
            doRequest(new UploadAvatarCommand(typedFile),
                    this::onSuccess);

        } else {
            Log.e(ProfilePresenter.class.getSimpleName(), error);
        }
    };

    private ImagePickCallback coverCallback = (fragment, image, error) -> {
        if (image != null) {
            view.setCoverImage(Uri.fromFile(new File(image.getFileThumbnail())));

            UserSession userSession = this.appSessionHolder.get().get();
            User user = userSession.getUser();

            user.setCoverPath(image.getFileThumbnail());

            this.appSessionHolder.put(userSession);

            eventBus.post(new UpdateUserInfoEvent());
        }
    };

    public ProfilePresenter(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        TrackingHelper.profile(getUserId());
    }

    @Override
    public void resume() {
        super.resume();

        User user = this.appSessionHolder.get().get().getUser();
        view.setUserName(user.getUsername());
        view.setUserEmail(user.getEmail());
        view.setUserId(user.getUsername());
        view.setLivesIn(user.getLocation());
        view.setFrom(user.getLocation());

        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.setCoverImage(Uri.fromFile(new File(user.getCoverPath())));
    }

    @Override
    public void handleError(SpiceException error) {
        view.avatarProgressVisible(false);
        super.handleError(error);
    }

    private void onSuccess(User obj) {
        UserSession userSession = appSessionHolder.get().get();
        User user = userSession.getUser();
        user.setAvatar(obj.getAvatar());

        appSessionHolder.put(userSession);
        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.avatarProgressVisible(false);
        eventBus.post(new UpdateUserInfoEvent());
    }

    public void logout() {
        this.appSessionHolder.destroy();
        snappyRepository.clearAll();
        activityRouter.finish();
        activityRouter.openLogin();
    }

    public void onDataSet(int year, int month, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar calendar = new GregorianCalendar(year, month, day);
        view.setDateOfBirth(sdf.format(calendar.getTime()));
    }

    //don't use of get PREFIX
    public ImagePickCallback provideAvatarChooseCallback() {
        return avatarCallback;
    }

    public ImagePickCallback provideCoverChooseCallback() {
        return coverCallback;
    }


    public static interface View extends Presenter.View {
        public void setAvatarImage(Uri uri);

        public void setCoverImage(Uri uri);

        void avatarProgressVisible(boolean visible);

        void setDateOfBirth(String format);

        void setFrom(String location);

        void setUserName(String username);

        void setUserEmail(String email);

        void setUserId(String username);

        void setLivesIn(String location);
    }
}
