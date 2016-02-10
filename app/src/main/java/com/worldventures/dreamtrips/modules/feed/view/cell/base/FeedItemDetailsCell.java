package com.worldventures.dreamtrips.modules.feed.view.cell.base;

import android.app.Dialog;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.popup.FeedItemMenuBuilder;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedItemCommonDataHelper;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class FeedItemDetailsCell<T extends FeedItem> extends BaseFeedCell<T> {

    FeedItemCommonDataHelper feedItemCommonDataHelper;

    @InjectView(R.id.edit_feed_item)
    ImageView editFeedItem;

    @Inject
    @Named(RouteCreatorModule.PROFILE)
    RouteCreator<Integer> routeCreator;

    private WeakHandler handler;

    public FeedItemDetailsCell(View view) {
        super(view);
        feedItemCommonDataHelper = new FeedItemCommonDataHelper(view.getContext());
        feedItemCommonDataHelper.attachView(view);
        handler = new WeakHandler();
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        feedItemCommonDataHelper.set(getModelObject(), sessionHolder.get().get().getUser().getId(), false);
        feedItemCommonDataHelper.setOnEditClickListener(view -> onMore());
    }

    public void openItemDetails() {
        Route detailsRoute = Route.FEED_ITEM_DETAILS;
        FeedDetailsBundle bundle = new FeedDetailsBundle(getModelObject());
        if (tabletAnalytic.isTabletLandscape()) {
            bundle.setSlave(true);
        }
        router.moveTo(detailsRoute, NavigationConfigBuilder.forActivity()
                .data(bundle)
                .build());
    }

    protected void showMoreDialog(@MenuRes int menuRes, @StringRes int headerDelete, @StringRes int textDelete) {
        editFeedItem.setEnabled(false);
        FeedItemMenuBuilder.create(itemView.getContext(), editFeedItem, menuRes)
                .onDelete(() -> showDeleteDialog(headerDelete, textDelete))
                .onEdit(this::onEdit)
                .show();
        handler.postDelayed(() -> editFeedItem.setEnabled(true), 500);
    }

    private void showDeleteDialog(@StringRes int headerDelete, @StringRes int textDelete) {
        Dialog dialog = new SweetAlertDialog(itemView.getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(itemView.getResources().getString(headerDelete))
                .setContentText(itemView.getResources().getString(textDelete))
                .setConfirmText(itemView.getResources().getString(R.string.post_delete_confirm))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    onDelete();
                });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    protected void onDelete() {
        sendAnalyticEvent(TrackingHelper.ATTRIBUTE_DELETE);
    }

    protected void onEdit() {
        sendAnalyticEvent(TrackingHelper.ATTRIBUTE_EDIT);
    }

    private void sendAnalyticEvent(String eventType) {
        FeedItem feedItem = getModelObject();
        getEventBus().post(new FeedItemAnalyticEvent(eventType, feedItem.getItem().getUid(), feedItem.getType()));
    }

    protected abstract void onMore();

    @Optional
    @OnClick(R.id.feed_header_avatar)
    void eventOwnerClicked() {
        User user = getModelObject().getLinks().getUsers().get(0);
        router.moveTo(routeCreator.createRoute(user.getId()), NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(new UserBundle(user))
                .build());
    }

}
