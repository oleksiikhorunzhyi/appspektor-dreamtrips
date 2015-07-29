package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

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

    @InjectView(R.id.profile_toolbar)
    protected Toolbar profileToolbar;

    @InjectView(R.id.profile_toolbar_title)
    protected TextView profileToolbarTitle;

    @InjectView(R.id.profile_user_status)
    protected TextView profileToolbarUserStatus;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;


    @InjectView(R.id.feedview)
    FeedView feedView;

    protected ProfileView profileView;
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
        profileView.setOnCreatePostClick(() -> getPresenter().makePost());

        profileView.setOnFeedReload(() -> getPresenter().loadFeed());
        feedView.setup(injectorProvider, savedInstanceState);
        feedView.setHeader(profileView);

        feedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = feedView.getLayoutManager().getItemCount();
                int lastVisibleItemPosition = feedView.getLayoutManager().findLastVisibleItemPosition();
                getPresenter().scrolled(itemCount, lastVisibleItemPosition);
            }
        });
        feedView.setOnParallaxScroll((percentage, offset, parallax) -> {
            setToolbarAlpha(percentage);
            if (percentage > 0.42f) {
                profileToolbarTitle.setVisibility(View.VISIBLE);
                profileToolbarUserStatus.setVisibility(View.VISIBLE);
                profileView.getUserName().setVisibility(View.INVISIBLE);
                profileView.getUserStatus().setVisibility(View.INVISIBLE);
            } else {
                profileToolbarTitle.setVisibility(View.INVISIBLE);
                profileToolbarUserStatus.setVisibility(View.INVISIBLE);
                profileView.getUserName().setVisibility(View.VISIBLE);
                profileView.getUserStatus().setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (profileToolbar != null) setToolbarAlpha(feedView.getParallaxPrecentage());
    }

    private void setToolbarAlpha(float percentage) {
        Drawable c = profileToolbar.getBackground();
        int round = Math.round(percentage * 255);
        c.setAlpha(round);
        profileToolbar.setBackgroundDrawable(c);
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
        profileToolbarTitle.setText(username);
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
    public void setSocial(Boolean isEnabled) {
        profileView.getAddFriend().setEnabled(isEnabled);
        profileView.getFriendRequest().setEnabled(isEnabled);
        profileView.getFriendRequest().setEnabled(isEnabled);
    }

    @Override
    public void setMember() {
        setUserStatus(R.color.white, R.string.empty, 0);
    }

    @Override
    public void setGold() {
        setUserStatus(R.color.golden_user, R.string.profile_golden, R.drawable.ic_profile_gold_member);
    }

    @Override
    public void setPlatinum() {
        setUserStatus(R.color.platinum_user, R.string.profile_platinum, R.drawable.ic_profile_platinum_member);
    }

    private void setUserStatus(int color, int title, int drawable) {
        profileView.getUserStatus().setTextColor(getResources().getColor(color));
        profileView.getUserStatus().setText(title);
        profileView.getUserStatus().setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);

        profileToolbarUserStatus.setTextColor(getResources().getColor(color));
        profileToolbarUserStatus.setText(title);
        profileToolbarUserStatus.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    @Override
    public void onRefresh() {
        getPresenter().onRefresh();
    }

    @Override
    public void startLoading() {
        weakHandler.postDelayed(() -> {
            profileView.getProfileFeedReload().setVisibility(View.GONE);
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        }, 100);
    }

    @Override
    public void finishLoading() {
        weakHandler.postDelayed(() -> {
            if (swipeContainer != null) {
                swipeContainer.setRefreshing(false);
            }
        }, 100);
    }

    @Override
    public void onFeedError() {
        weakHandler.postDelayed(() -> {
            profileView.getProfileFeedReload().setVisibility(View.VISIBLE);
        }, 100);

    }

    @Override
    public void setFriendButtonText(@StringRes int res) {
        profileView.getFriends().setText(res);
    }

    @Override
    public IRoboSpiceAdapter<BaseFeedModel> getAdapter() {
        return feedView.getAdapter();
    }
}
