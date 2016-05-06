package com.worldventures.dreamtrips.modules.tripsimages.view.fragment.singlefullscreen;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolder;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.PhotoTagHolderManager;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.modules.trips.event.TripImageAnalyticEvent;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.EditPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.events.SocialViewPagerStateChangedEvent;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.SocialImageFullscreenPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.FullScreenPhotoActionPanelDelegate;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import icepick.Icepick;

@Layout(R.layout.fragment_fullscreen_photo)
public class SocialImageFullscreenFragment extends FullScreenPhotoFragment<SocialImageFullscreenPresenter, Photo> implements SocialImageFullscreenPresenter.View, Flaggable, FullScreenPhotoActionPanelDelegate.ContentVisibilityListener {

    FullScreenPhotoActionPanelDelegate viewDelegate = new FullScreenPhotoActionPanelDelegate();

    //For resolving Fresco onFinalImageSet callback double launch (here onImageGlobalLayout() method)

    @InjectView(R.id.flag)
    protected FlagView flag;
    @InjectView(R.id.taggable_holder)
    protected PhotoTagHolder photoTagHolder;
    @InjectView(R.id.tag)
    protected ImageView tag;

    private PhotoTagHolderManager photoTagHolderManager;
    @Inject
    SnappyRepository db;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        viewDelegate.setup(getActivity(), rootView, getPresenter().getAccount(), injectorProvider.get());
        viewDelegate.setContentVisibilityListener(this);
    }

    @Override
    protected SocialImageFullscreenPresenter createPresenter(Bundle savedInstanceState) {
        return new SocialImageFullscreenPresenter((Photo) getArgs().getPhoto(), getArgs().getType());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(viewDelegate, outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(viewDelegate, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        //it is ok sync and send message to sync
        syncContentWrapperViewGroupWithGlobalState();
        eventBus.post(new SocialViewPagerStateChangedEvent());
    }

    @Override
    public void setContent(IFullScreenObject photo) {
        if (photo.getUser() == null) return;
        super.setContent(photo);
        viewDelegate.setContent((Photo) photo);
    }

    @Override
    public void openEdit(EditPhotoBundle bundle) {
        int containerId = R.id.container_details_floating;
        router.moveTo(Route.EDIT_PHOTO, NavigationConfigBuilder.forRemoval()
                .containerId(containerId)
                .fragmentManager(getFragmentManager())
                .build());
        router.moveTo(Route.EDIT_PHOTO, NavigationConfigBuilder.forFragment()
                .containerId(containerId)
                .backStackEnabled(false)
                .fragmentManager(getFragmentManager())
                .data(bundle)
                .build());
    }

    @Override
    public void showFlagDialog(List<Flag> flags) {
        hideProgress();
        flag.showFlagsPopup(flags, (flagReasonId, reason) -> getPresenter().sendFlagAction(flagReasonId, reason));
    }

    @Override
    public void showProgress() {
        flag.showProgress();
    }

    @Override
    public void hideProgress() {
        flag.hideProgress();
    }

    @Override
    public void showContentWrapper() {
        SocialViewPagerState state = getState();
        saveViewState(true, state.isTagHolderVisible());
        syncContentWrapperViewGroupWithGlobalState();
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

    @OnClick(R.id.tag)
    public void onTag() {
        SocialViewPagerState state = getState();
        saveViewState(state.isContentWrapperVisible(), !photoTagHolder.isShown());
        eventBus.post(new SocialViewPagerStateChangedEvent());
    }

    protected void hideTagViewGroup() {
        tag.setSelected(false);
        photoTagHolderManager.hide();
        ivImage.setScaleEnabled(true);
    }

    protected void showTagViewGroup() {
        tag.setSelected(true);
        photoTagHolderManager.show(ivImage);
        ivImage.setScaleEnabled(false);
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
    protected void onImageGlobalLayout() {
        if (isResumed()) {
            Photo photo = getPresenter().getPhoto();
            if (photo != null) {
                syncTagViewGroupWithGlobalState();
            }
        }
    }

    @Override
    public void onVisibilityChange() {
        SocialViewPagerState state = getState();
        saveViewState(!state.isContentWrapperVisible(), state.isTagHolderVisible());
        eventBus.post(new SocialViewPagerStateChangedEvent());
    }

    public void onEvent(SocialViewPagerStateChangedEvent event) {
        if (isResumed()) {
            syncTagViewGroupWithGlobalState();
            syncContentWrapperViewGroupWithGlobalState();
        }
    }

    private void syncContentWrapperViewGroupWithGlobalState() {
        if (getState().isContentWrapperVisible()) {
            viewDelegate.showContent();
        } else {
            viewDelegate.hideContent();
        }
    }

    private void syncTagViewGroupWithGlobalState() {
        SocialViewPagerState state = getState();
        photoTagHolderManager = new PhotoTagHolderManager(photoTagHolder, getPresenter().getAccount(), getPresenter().getPhoto().getUser());
        if (state.isTagHolderVisible() && !getPresenter().getPhoto().getPhotoTags().isEmpty()) {
            photoTagHolder.removeAllViews();
            if (getPresenter().getPhoto() != null) {
                showTagViewGroup();
                photoTagHolderManager.addExistsTagViews(getPresenter().getPhoto().getPhotoTags());
                photoTagHolderManager.setTagDeletedListener(photoTag -> getPresenter().deleteTag(photoTag));
            }
        } else {
            hideTagViewGroup();
        }

        manageTagIconVisibility();
    }

    private void manageTagIconVisibility() {
        if (getPresenter().getPhoto().getPhotoTagsCount() == 0) {
            tag.setVisibility(View.GONE);
        } else {
            tag.setVisibility(View.VISIBLE);
        }
    }

    private SocialViewPagerState getState() {
        SocialViewPagerState state = db.getSocialViewPagerState();
        return state == null ? new SocialViewPagerState() : state;
    }

    private void saveViewState(boolean showContent, boolean showTag) {
        SocialViewPagerState state = new SocialViewPagerState();
        state.setContentWrapperVisible(showContent);
        state.setTagHolderVisible(showTag);
        db.saveSocialViewPagerState(state);
    }

    @Override
    public void flagSentSuccess() {
        informUser(R.string.flag_sent_success_msg);
    }
}