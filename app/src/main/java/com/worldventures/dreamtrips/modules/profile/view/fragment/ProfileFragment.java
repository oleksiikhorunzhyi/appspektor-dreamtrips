package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedView;
import com.worldventures.dreamtrips.modules.profile.presenter.ProfilePresenter;
import com.worldventures.dreamtrips.modules.profile.view.custom.ProfileView;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import icepick.Icicle;


public abstract class ProfileFragment<T extends ProfilePresenter> extends BaseFragment<T>
        implements ProfilePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Icicle
    String filePath;
    @Icicle
    int callbackType;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    ProfileView profileView;

    @InjectView(R.id.feedview)
    FeedView feedView;

    private WeakHandler weakHandler;
    private Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);
        layoutConfiguration();
        profileView = new ProfileView(getActivity());
        profileView.setOnBucketListClicked(() -> getPresenter().openBucketList());
        profileView.setOnTripImageClicked(() -> getPresenter().openTripImages());
        profileView.setOnFriendsClicked(() -> getPresenter().openFriends());
        feedView.setup(injectorProvider, savedInstanceState);
        feedView.setHeader(profileView);
    }

    private void layoutConfiguration() {
        swipeContainer.setOnRefreshListener(this);

    }

    @Override
    public void setAvatarImage(Uri uri) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (uri != null) {
                    SimpleDraweeView draweeView = profileView.getUserPhoto();
                    setImage(uri, draweeView);
                }
            });
    }

    @Override
    public void setCoverImage(Uri uri) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                if (uri != null) {
                    SimpleDraweeView draweeView = profileView.getUserCover();
                    setImage(uri, draweeView);
                }
            });
    }

    private void setImage(Uri uri, SimpleDraweeView draweeView) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        if (draweeView.getTag() != null) {
            builder.setLowResImageRequest(ImageRequest.fromUri((Uri) draweeView.getTag()));
        }
        builder.setImageRequest(ImageRequest.fromUri(uri));
        DraweeController dc = builder.build();
        draweeView.setController(dc);
        draweeView.setTag(uri);
    }

    @Override
    public void setDateOfBirth(String format) {
        profileView.getDateOfBirth().setText(format);
    }

    @Override
    public void setFrom(String location) {
        profileView.getEtFrom().setText(location);
    }

    @Override
    public void setUserName(String username) {
        profileView.getUserName().setText(username);
    }

    @Override
    public void setUserId(String username) {
        profileView.getEtUserId().setText(username);
    }

    @Override
    public void setEnrollDate(String date) {
        profileView.getEtEnroll().setText(date);
    }

    @Override
    public void setTripImagesCount(int count) {
        profileView.getTripImages().setText(String.format(getString(R.string.profile_trip_images), count));
    }

    @Override
    public void setTripsCount(int count) {
        profileView.getTrips().setText(String.format(getString(R.string.profile_dream_trips), count));
    }

    @Override
    public void setBucketItemsCount(int count) {
        profileView.getBuckets().setText(String.format(getString(R.string.profile_bucket_list), count));
    }

    @Override
    public void setMember() {
        profileView.getUserStatus().setTextColor(getResources().getColor(R.color.white));
        profileView.getUserStatus().setText("");
        profileView.getUserStatus().setCompoundDrawablesWithIntrinsicBounds(0,
                0, 0, 0);
    }

    @Override
    public void setGold() {
        profileView.getUserStatus().setTextColor(getResources().getColor(R.color.golden_user));
        profileView.getUserStatus().setText(R.string.profile_golden);
        profileView.getUserStatus().setCompoundDrawablesWithIntrinsicBounds(R.drawable.gold_member,
                0, 0, 0);
    }

    @Override
    public void setPlatinum() {
        profileView.getUserStatus().setTextColor(getResources().getColor(R.color.platinum_user));
        profileView.getUserStatus().setText(R.string.profile_platinum);
        profileView.getUserStatus().setCompoundDrawablesWithIntrinsicBounds(R.drawable.platinum_member,
                0, 0, 0);
    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null)
                swipeContainer.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null)
                swipeContainer.setRefreshing(false);
        });
    }


    @Override
    public IRoboSpiceAdapter<BaseFeedModel> getAdapter() {
        return feedView.getAdapter();
    }
}
