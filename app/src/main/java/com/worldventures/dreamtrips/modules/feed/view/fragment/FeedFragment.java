package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgeImageView;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedAdditionalInfoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.SuggestedPhotosCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.SuggestedPhotosDelegate;
import com.worldventures.dreamtrips.modules.feed.view.util.CirclesFilterPopupWindow;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import butterknife.Optional;
import timber.log.Timber;

@Layout(R.layout.fragment_feed)
@MenuResource(R.menu.menu_activity_feed)
public class FeedFragment extends BaseFeedFragment<FeedPresenter, FeedBundle>
        implements FeedPresenter.View, SwipeRefreshLayout.OnRefreshListener, SuggestedPhotosDelegate {

    BadgeImageView friendsBadge;
    BadgeImageView unreadConversationBadge;

    private CirclesFilterPopupWindow filterPopupWindow;

    private ContentObserver contentObserver;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        restorePostIfNeeded();

        if (isTabletLandscape()) {
            router.moveTo(Route.FEED_LIST_ADDITIONAL_INFO, NavigationConfigBuilder.forFragment()
                    .backStackEnabled(false)
                    .fragmentManager(getChildFragmentManager())
                    .containerId(R.id.additional_info_container)
                    .data(new FeedAdditionalInfoBundle(getPresenter().getAccount()))
                    .build());
        }

        adapter.registerCell(MediaAttachment.class, SuggestedPhotosCell.class);
        ((BaseDelegateAdapter) adapter).registerDelegate(MediaAttachment.class, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        TrackingHelper.viewActivityFeedScreen();
    }

    private void restorePostIfNeeded() {

    }

    @Override
    protected void onMenuInflated(Menu menu) {
        super.onMenuInflated(menu);
        MenuItem friendsItem = menu.findItem(R.id.action_friend_requests);
        friendsBadge = (BadgeImageView) MenuItemCompat.getActionView(friendsItem);
        setRequestsCount(getPresenter().getFriendsRequestsCount());
        friendsBadge.setOnClickListener(v -> {
            router.moveTo(Route.FRIENDS, NavigationConfigBuilder.forActivity()
                    .data(new FriendMainBundle(FriendMainBundle.REQUESTS))
                    .build());
            TrackingHelper.tapFeedButton(TrackingHelper.ATTRIBUTE_OPEN_FRIENDS);
        });

        MenuItem conversationItem = menu.findItem(R.id.action_unread_conversation);
        if (conversationItem != null) {
            unreadConversationBadge = (BadgeImageView) MenuItemCompat.getActionView(conversationItem);
            unreadConversationBadge.setImage(R.drawable.messenger_icon_white);
            unreadConversationBadge.setBadgeValue(getPresenter().getUnreadConversationCount());
            unreadConversationBadge.setOnClickListener(v -> getPresenter().onUnreadConversationsClick());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (filterPopupWindow == null || filterPopupWindow.dismissPassed()) {
                    actionFilter();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        filterPopupWindow = null;
    }

    @Override
    public void onDestroyView() {
        try {
            getContext().getContentResolver().unregisterContentObserver(contentObserver);
        } catch (Exception e) {
            //hot fix solution to prevent crash on feed fragment. Will be fixed 26.04 12:00
            Timber.e(e, "");
        }
        super.onDestroyView();
    }

    private void actionFilter() {
        FeedPresenter presenter = getPresenter();
        View menuItemView = getActivity().findViewById(R.id.action_filter);
        filterPopupWindow = new CirclesFilterPopupWindow(getContext());
        filterPopupWindow.setCircles(presenter.getFilterCircles());
        filterPopupWindow.setAnchorView(menuItemView);
        filterPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            filterPopupWindow.dismiss();
            presenter.applyFilter((Circle) parent.getItemAtPosition(position));
        });
        filterPopupWindow.show();
        filterPopupWindow.setCheckedCircle(presenter.getAppliedFilterCircle());
    }

    @Override
    protected FeedPresenter createPresenter(Bundle savedInstanceState) {
        return new FeedPresenter();
    }

    @Override
    public BaseArrayListAdapter createAdapter() {
        return new BaseDelegateAdapter<>(feedView.getContext(), this);
    }

    private void openPost() {
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
                .containerId(R.id.container_details_floating)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .build());
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .containerId(R.id.container_details_floating)
                .build());
    }

    private void openSharePhoto(CreateEntityBundle bundle) {
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forRemoval()
                .containerId(R.id.container_details_floating)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .build());
        router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
                .backStackEnabled(false)
                .fragmentManager(getActivity().getSupportFragmentManager())
                .containerId(R.id.container_details_floating)
                .data(bundle)
                .build());
    }

    @Override
    public void setRequestsCount(int count) {
        if (friendsBadge != null) {
            friendsBadge.setBadgeValue(count);
        }
    }

    @Override
    public void setUnreadConversationCount(int count) {
        if (unreadConversationBadge != null) {
            unreadConversationBadge.setBadgeValue(count);
        }
    }

    @Override
    public void refreshFeedItems(List<FeedItem> feedItems, List<PhotoGalleryModel> suggestedPhotos, boolean needLoader) {
        if (isNeedAddSuggestions(suggestedPhotos.size())) {
            List listWithSuggestion = new ArrayList<>(feedItems.size() + 1);
            listWithSuggestion.add(new MediaAttachment(suggestedPhotos, PickImageDelegate.PICK_PICTURE, -1));
            listWithSuggestion.addAll(feedItems);

            adapter.clear();
            refreshFeedItems(listWithSuggestion, needLoader);
            return;
        }
        refreshFeedItems(feedItems, needLoader);
    }

    @Override
    public void refreshFeedItems(List feedItems, boolean needLoader) {
        if (isNeedToSaveSuggestions()) {
            List listWithSuggestion = new ArrayList<>();
            listWithSuggestion.add(0, adapter.getItem(0));
            listWithSuggestion.addAll(feedItems);
            super.refreshFeedItems(listWithSuggestion, needLoader);
            return;
        }
        super.refreshFeedItems(feedItems, needLoader);
    }

    @Optional
    @OnClick(R.id.share_post)
    protected void onPostClicked() {
        openPost();
    }

    @Optional
    @OnClick(R.id.share_photo)
    protected void onSharePhotoClick() {
        openSharePhoto(new CreateEntityBundle(true));
    }

    @Override
    public void onCancelClicked() {
        getPresenter().removeSuggestedPhotos();
    }

    @Override
    public void onAttachClicked(List<PhotoGalleryModel> pickedItems) {
        openSharePhoto(new CreateEntityBundle(new MediaAttachment(pickedItems, PickImageDelegate.PICK_PICTURE)));
        getPresenter().removeSuggestedPhotos();
    }

    @Override
    public void onRegisterObserver(ContentObserver contentObserver) {
        this.contentObserver = contentObserver;
        getContext().getContentResolver()
                .registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, contentObserver);
    }

    @Override
    public void onCellClicked(MediaAttachment model) {
        // nothing to do
    }

    private boolean isNeedAddSuggestions(int suggestedPhotosSize) {
        boolean isAdapterEmpty = adapter.getCount() == 0;
        boolean isAdapterContainsSuggestions = !isAdapterEmpty && adapter.getItem(0) instanceof MediaAttachment;
        return !isAdapterEmpty && suggestedPhotosSize > 0 || isAdapterContainsSuggestions;
    }

    private boolean isNeedToSaveSuggestions() {
        return adapter.getCount() > 0 && adapter.getItem(0) instanceof MediaAttachment
                && getPresenter().isHasNewPhotos(((MediaAttachment) adapter.getItem(0)).chosenImages);
    }
}
