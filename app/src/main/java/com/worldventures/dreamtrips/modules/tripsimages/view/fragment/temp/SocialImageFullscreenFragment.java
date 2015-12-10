package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.modules.trips.event.TripImageAnalyticEvent;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.SocialImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.FullScreenPhotoActionPanelDelegate;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


@Layout(R.layout.fragment_fullscreen_photo)
public class SocialImageFullscreenFragment extends FullScreenPhotoFragment<SocialImageFullscreenPresenter, Photo> implements SocialImageFullscreenPresenter.View, Flaggable {

    FullScreenPhotoActionPanelDelegate viewDelegate = new FullScreenPhotoActionPanelDelegate();

    @InjectView(R.id.flag)
    protected FlagView flag;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        viewDelegate.setup(getActivity(), rootView, getPresenter().getAccount());
    }

    @Override
    protected SocialImageFullscreenPresenter createPresenter(Bundle savedInstanceState) {
        return new SocialImageFullscreenPresenter((Photo) getArgs().getPhoto(), getArgs().getTab());
    }

    private void deletePhoto() {
        eventBus.post(new TripImageAnalyticEvent(getArgs().getPhoto().getFSId(), TrackingHelper.ATTRIBUTE_DELETE_IMAGE));
        Dialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.photo_delete))
                .setContentText(getResources().getString(R.string.photo_delete_caption))
                .setConfirmText(getResources().getString(R.string.post_delete_confirm))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    getPresenter().onDeleteAction();
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    @Override
    public void openEdit(EditPhotoBundle bundle) {
        NavigationBuilder.create()
                .with(activityRouter)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(bundle)
                .attach(Route.PHOTO_EDIT);
    }


    @Override
    public void showFlagDialog(List<Flag> flags) {
        hideProgress();
        flag.showFlagsPopup(flags, (flagReasonId, reason) -> getPresenter().sendFlagAction(flagReasonId, reason));
    }

    @Override
    public void setContent(IFullScreenObject photo) {
        super.setContent(photo);
        viewDelegate.setContent((Photo) photo);
    }


    @OnClick(R.id.iv_share)
    public void actionShare() {
        eventBus.post(new TripImageAnalyticEvent(getArgs().getPhoto().getFSId(), TrackingHelper.ATTRIBUTE_SHARE_IMAGE));
        new ShareDialog(getActivity(), type -> getPresenter().onShare(type)).show();
    }

    @OnClick({R.id.iv_comment, R.id.tv_comments_count})
    public void actionComment() {
        getPresenter().onCommentsAction();
    }

    @OnClick(R.id.tv_likes_count)
    public void actionLikes() {
        getPresenter().onLikesAction();
    }


    @OnClick(R.id.iv_like)
    public void actionLike() {
        eventBus.post(new TripImageAnalyticEvent(getArgs().getPhoto().getFSId(), TrackingHelper.ATTRIBUTE_LIKE_IMAGE));
        getPresenter().onLikeAction();
    }

    @OnClick(R.id.edit)
    public void actionEdit(View view) {
        view.setEnabled(false);
        FeedItemMenuBuilder.create(getActivity(), view, R.menu.menu_feed_entity_edit)
                .onDelete(this::deletePhoto)
                .onEdit(() -> {
                    if (isVisibleOnScreen()) {
                        eventBus.post(new TripImageAnalyticEvent(getArgs().getPhoto().getFSId(), TrackingHelper.ATTRIBUTE_EDIT_IMAGE));
                        getPresenter().onEdit();
                    }
                })
                .dismissListener(menu -> view.setEnabled(true))
                .show();
    }

    @OnClick(R.id.user_photo)
    void onUserClicked() {
        getPresenter().onUserClicked();
    }

    @OnClick(R.id.flag)
    public void actionFlag() {
        eventBus.post(new TripImageAnalyticEvent(getArgs().getPhoto().getFSId(), TrackingHelper.ATTRIBUTE_FLAG_IMAGE));
        getPresenter().onFlagAction(this);
    }

    @Override
    public void showProgress() {
        flag.showProgress();
    }

    @Override
    public void hideProgress() {
        flag.hideProgress();
    }

}
