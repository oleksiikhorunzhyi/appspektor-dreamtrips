package com.worldventures.dreamtrips.modules.profile.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedQuery;
import com.worldventures.dreamtrips.modules.feed.api.GetUserFeedQuery;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.friends.api.GetCirclesQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class ProfilePresenter<T extends ProfilePresenter.View> extends Presenter<T> {

    protected User user;

    @Inject
    SnappyRepository snappyRepository;

    private DreamSpiceAdapterController<BaseFeedModel> adapterController = new DreamSpiceAdapterController<BaseFeedModel>() {
        @Override
        public SpiceRequest<ArrayList<BaseFeedModel>> getReloadRequest() {
            return new GetUserFeedQuery(user.getId(), 0);
        }

        @Override
        public SpiceRequest<ArrayList<BaseFeedModel>> getNextPageRequest(int currentCount) {
            return new GetUserFeedQuery(user.getId(), currentCount / GetFeedQuery.LIMIT + 1);
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
                    handleError(spiceException);
                }
            }
        }
    };

    public ProfilePresenter() {
    }

    public ProfilePresenter(User user) {
        this.user = user;
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        setUserProfileInfo();
        loadProfile();
        loadCircles();
    }

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() <= 1/*Header*/) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
        }
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

    protected void onProfileLoaded(User user) {
        this.user = user;
        //
        setUserProfileInfo();
        view.setTripImagesCount(user.getTripImagesCount());
        view.setBucketItemsCount(user.getBucketListItemsCount());

        loadFeed();

    }

    private void loadFeed() {
        adapterController.reload();
    }

    protected abstract void loadProfile();

    public void openFriends() {
        activityRouter.openFriends();
    }

    ///Circles

    private void loadCircles() {
        doRequest(new GetCirclesQuery(), this::saveCircles);
    }

    private void saveCircles(List<Circle> circles) {
        snappyRepository.saveCircles(circles);
    }

    public interface View extends Presenter.View {
        Bundle getArguments();

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

        void setBucketItemsCount(int count);

        void setGold();

        void setPlatinum();

        void setMember();

        IRoboSpiceAdapter<BaseFeedModel> getAdapter();
    }
}
