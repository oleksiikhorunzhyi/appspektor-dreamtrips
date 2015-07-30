package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.NewPostCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.ImagePickCallback;

import java.io.File;

import icepick.Icicle;

public class PostPresenter extends Presenter<PostPresenter.View> {

    @Icicle
    String input;

    protected ImagePickCallback selectImageCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.fromFile(new File(image.getFileThumbnail()));
            handlePhotoPick(uri);
        }
    };
    protected ImagePickCallback fbCallback = (fragment, image, error) -> {
        if (error != null) {
            view.informUser(error);
        } else {
            Uri uri = Uri.parse(image.getFilePathOriginal());
            handlePhotoPick(uri);
        }
    };

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setName(getAccount().getFullName());
        view.setAvatar(getAccount().getAvatar().getThumb());
    }

    public void cancel() {
        fragmentCompass.removePost();
    }

    public void post() {
        if (!TextUtils.isEmpty(input))
            doRequest(new NewPostCommand(input), jsonObject -> {
            });
    }

    public void postInputChanged(String input) {
        this.input = input;
        if (!TextUtils.isEmpty(input))
            view.enableButton();
        else view.disableButton();
    }

    private void handlePhotoPick(Uri uri) {
        view.attachPhoto(uri);
    }

    public ImagePickCallback provideSelectImageCallback() {
        return selectImageCallback;
    }

    public ImagePickCallback provideFbCallback() {
        return fbCallback;
    }

    public interface View extends Presenter.View {
        void setName(String userName);

        void setAvatar(String avatarUrl);

        void enableButton();

        void disableButton();

        void attachPhoto(Uri uri);
    }
}
