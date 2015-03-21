package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.util.Log;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.profile.UploadAvatarRequest;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresentation;
import com.worldventures.dreamtrips.core.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.mime.TypedFile;

public class ProfileFragmentPresentation extends BasePresentation<ProfileFragmentPresentation.View> {

    @Inject
    protected Prefs prefs;

    @Inject
    @Global
    protected EventBus eventBus;

    private ImagePickCallback avatarCallback = (fragment, image, error) -> {
        if (image != null) {
            final File file = new File(image.getFileThumbnail());
            final TypedFile typedFile = new TypedFile("image/*", file);
            view.avatarProgressVisible(true);
            dreamSpiceManager.execute(new UploadAvatarRequest(typedFile), new RequestListener<User>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    view.avatarProgressVisible(false);
                    handleError(spiceException);
                    view.informUser(context.getString(R.string.error_internal_server));
                }

                @Override
                public void onRequestSuccess(User obj) {
                    UserSession userSession = appSessionHolder.get().get();
                    User user = userSession.getUser();
                    user.setAvatar(obj.getAvatar());

                    appSessionHolder.put(userSession);
                    view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
                    view.avatarProgressVisible(false);
                    eventBus.post(new UpdateUserInfoEvent());
                }
            });


        } else {
            Log.e(ProfileFragmentPresentation.class.getSimpleName(), error);
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

    public ProfileFragmentPresentation(View view) {
        super(view);
    }

    @Override
    public void init() {
        super.init();
        AdobeTrackingHelper.profile(getUserId());
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

    public void logout() {
        this.appSessionHolder.destroy();
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


    public static interface View extends BasePresentation.View {
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
