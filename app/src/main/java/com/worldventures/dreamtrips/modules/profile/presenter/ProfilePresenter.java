package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.support.annotation.StringRes;
import android.text.format.DateFormat;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedQuery;
import com.worldventures.dreamtrips.modules.feed.event.CommentsPressedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class ProfilePresenter<T extends ProfilePresenter.View, U extends User> extends Presenter<T> {

    protected U user;

    private int previousTotal = 0;
    private boolean loading = true;

    @Inject
    SnappyRepository snappyRepository;

    protected DreamSpiceAdapterController<BaseFeedModel> adapterController = new DreamSpiceAdapterController<BaseFeedModel>() {
        @Override
        public SpiceRequest<ArrayList<BaseFeedModel>> getReloadRequest() {
            return getRefreshRequest();
        }

        @Override
        public SpiceRequest<ArrayList<BaseFeedModel>> getNextPageRequest(int currentCount) {
            return ProfilePresenter.this.getNextPageRequest(currentCount / GetFeedQuery.LIMIT);
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<BaseFeedModel> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading();
                if (spiceException != null) {
                    view.onFeedError();
                }
                if (type.equals(LoadType.RELOAD)) {
                    resetLazyLoadFields();
                }
            }
        }
    };

    List<Circle> circles;

    public ProfilePresenter() {
    }

    public ProfilePresenter(U user) {
        this.user = user;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
        setUserProfileInfo();
        loadCircles();
        loadProfile();
        view.setFriendButtonText(featureManager.available(Feature.SOCIAL) ? R.string.profile_friends : R.string.coming_soon);
        checkPostShown();
    }

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() <= 1/*Header*/) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
        }
    }

    private void checkPostShown() {
        if (snappyRepository.hasPost()) makePost();
    }

    public abstract void openBucketList();

    public abstract void openTripImages();

    public void onRefresh() {
        loadProfile();
    }

    protected void setUserProfileInfo() {
        view.setUserName(user.getFullName());
        view.setDateOfBirth(DateTimeUtils.convertDateToString(user.getBirthDate(),
                DateFormat.getMediumDateFormat(context)));
        view.setEnrollDate(DateTimeUtils.convertDateToString(user.getEnrollDate(),
                DateFormat.getMediumDateFormat(context)));
        view.setUserId(user.getUsername());
        view.setFrom(user.getLocation());

        if (user.isGold())
            view.setGold();
        else if (user.isPlatinum())
            view.setPlatinum();
        else
            view.setMember();

        view.setAvatarImage(Uri.parse(user.getAvatar().getMedium()));
        view.setCoverImage(Uri.parse(user.getBackgroundPhotoUrl()));
    }

    protected void onProfileLoaded(U user) {
        this.user = user;
        //
        setUserProfileInfo();
        view.finishLoading();
        loadFeed();
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.finishLoading();
    }

    public void loadFeed() {
        if (featureManager.available(Feature.SOCIAL))
            adapterController.reload();
    }

    public void makePost() {
        view.openPost();
    }

    protected abstract void loadProfile();

    public void openFriends() {
        if (featureManager.available(Feature.SOCIAL)) {
            if (circles.isEmpty()) {
                view.startLoading();
                doRequest(new GetCirclesQuery(), circles -> {
                    view.finishLoading();
                    saveCircles(circles);
                    openFriends();
                });
            } else {
                view.openFriends();
            }
        }
    }

    ///Circles

    private void loadCircles() {
        if (featureManager.available(Feature.SOCIAL))
            doRequest(new GetCirclesQuery(), this::saveCircles);
    }

    protected void saveCircles(List<Circle> circles) {
        this.circles = circles;
        snappyRepository.saveCircles(circles);
    }

    private void resetLazyLoadFields() {
        previousTotal = 0;
        loading = false;
    }

    public void onEvent(CommentsPressedEvent event) {
        eventBus.cancelEventDelivery(event);
        view.openComments(event.getModel());
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL)) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
            if (!loading
                    && lastVisible == totalItemCount - 1
                    && (totalItemCount - 1) % GetFeedQuery.LIMIT == 0) {
                loading = true;
                adapterController.loadNext();
            }
        }
    }

    protected abstract SpiceRequest<ArrayList<BaseFeedModel>> getRefreshRequest();

    protected abstract SpiceRequest<ArrayList<BaseFeedModel>> getNextPageRequest(int count);

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        void setAvatarImage(Uri uri);

        void setCoverImage(Uri uri);

        void setDateOfBirth(String format);

        void setFrom(String location);

        void setUserName(String username);

        void setUserId(String username);

        void setEnrollDate(String date);

        void setTripImagesCount(int count);

        void setTripsCount(int count);

        void setSocial(Boolean isEnabled);

        void setBucketItemsCount(int count);

        void setGold();

        void setPlatinum();

        void setMember();

        BaseArrayListAdapter<BaseFeedModel> getAdapter();

        void onFeedError();

        void setFriendButtonText(@StringRes int res);

        void openComments(BaseFeedModel baseFeedModel);

        void openPost();

        void openFriends();
    }
}
