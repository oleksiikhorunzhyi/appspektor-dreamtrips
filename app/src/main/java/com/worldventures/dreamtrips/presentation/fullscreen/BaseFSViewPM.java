package com.worldventures.dreamtrips.presentation.fullscreen;

import android.content.Intent;
import android.net.Uri;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.model.FlagContent;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Image;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.presentation.BasePresentation;

import java.io.File;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

public abstract class BaseFSViewPM<T extends IFullScreenAvailableObject> extends BasePresentation<BaseFSViewPM.View> {

    @Inject
    DreamTripsApi dreamTripsApi;
    @Inject
    @Global
    EventBus eventBus;

    T photo;
    protected Type type;
    protected User user;

    public BaseFSViewPM(View view) {
        super(view);

    }

    public void setupPhoto(T photo) {
        this.photo = photo;
    }

    public void setupType(Type type) {
        this.type = type;
    }

    public T providePhoto() {
        return photo;
    }

    public void onCreate() {
        user = appSessionHolder.get().get().getUser();
    }

    public void onLikeAction() {

    }

    public final void setupActualViewState() {
        view.setTitle(photo.getFSTitle());
        view.setLiked(isLiked());
        view.setLikeVisibility(isLikeVisible());
        view.setDeleteVisibility(isDeleteVisible());
        view.setFlagVisibility(isFlagVisible());
        view.loadImage(photo.getFSImage());
        view.setInspireDescription(photo.getFsDescription());
    }

    protected abstract boolean isLiked();

    protected abstract boolean isFlagVisible();

    protected abstract boolean isDeleteVisible();

    protected abstract boolean isLikeVisible();

    public void showFlagAction(int itemId) {
        FlagContent flagContent = FlagContent.values()[itemId];
        if (flagContent.isNeedDescription()) {
            view.showFlagDescription(flagContent.getTitle());
        } else {
            view.showFlagConfirmDialog(flagContent.getTitle(), null);
        }
    }

    public void sendFlagAction(String title, String desc) {
    }

    public void onDeleteAction() {
    }

    public void onShareAction() {
        File file = DiskCacheUtils.findInCache(photo.getFSImage().getOriginal().getUrl(), ImageLoader.getInstance().getDiskCache());
        //  String file = ImageDownloader.Scheme.FILE.wrap(((Photo) photo).getImages().getOriginal().getUrl());
        Uri parse = Uri.fromFile(file);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, photo.getFsShareText());
        shareIntent.putExtra(Intent.EXTRA_STREAM, parse);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activityRouter.openShare(Intent.createChooser(shareIntent, "Share"));
    }

    public static BaseFSViewPM create(View view, IFullScreenAvailableObject photo) {
        if (photo instanceof Photo) {
            return new FSPhotoPM(view);
        } else if (photo instanceof Inspiration) {
            return new FSInspireMePM(view);
        }
        return null;
    }

    public static interface View extends BasePresentation.View {
        void setTitle(String title);

        void setInspireDescription(String desc);

        void setLiked(boolean isLiked);

        void loadImage(Image image);

        void setFlagVisibility(boolean isVisible);

        void setDeleteVisibility(boolean isVisible);

        void setLikeVisibility(boolean isVisible);

        public void showFlagConfirmDialog(String reason, String desc);

        public void showFlagDescription(String reason);
    }
}
