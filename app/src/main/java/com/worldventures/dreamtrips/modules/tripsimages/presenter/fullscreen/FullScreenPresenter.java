package com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen;

import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

public abstract class FullScreenPresenter<T extends IFullScreenObject, PRESENTER_VIEW extends FullScreenPresenter.View> extends Presenter<PRESENTER_VIEW> {

    protected TripImagesType type;
    protected T photo;

    public FullScreenPresenter(T photo, TripImagesType type) {
        this.photo = photo;
        this.type = type;
    }

    @Override
    public void takeView(PRESENTER_VIEW view) {
        super.takeView(view);
        setupActualViewState();
        TrackingHelper.view(type, String.valueOf(photo.getFSId()), getAccountUserId());
    }

    public void onEdit() {
    }

    public void onLikeAction() {
    }

    public void onFlagAction(Flaggable flaggable) {
    }

    public void onCommentsAction() {

    }

    public void onLikesAction() {

    }

    public void onUserClicked() {
        User user = photo.getUser();
        view.openUser(new UserBundle(user));
    }

    public final void setupActualViewState() {
        view.setContent(photo);
    }

    public void sendFlagAction(int flagReasonId, String reason) {
    }

    public void onDeleteAction() {
    }

    public void onShare(@ShareFragment.ShareType String type) {
        view.openShare(photo.getFSImage().getUrl(), photo.getFSShareText(), type);
        if (photo instanceof Inspiration) {
            TrackingHelper.insprShare(photo.getFSId(), type);
        }
    }

    public interface View extends Presenter.View {

        void openUser(UserBundle bundle);

        void openShare(String imageUrl, String text, @ShareFragment.ShareType String type);

        <T extends IFullScreenObject> void setContent(T photo);
    }
}
