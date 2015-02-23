package com.worldventures.dreamtrips.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.techery.spares.annotations.Layout;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;
import com.worldventures.dreamtrips.view.adapter.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.viewpager.FragmentItem;
import com.worldventures.dreamtrips.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.activity_full_screen_photo)
public class FullScreenPhotoActivity extends PresentationModelDrivenActivity<TripImagesListPM> implements TripImagesListPM.View {
    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String OUT_STATE_IMAGES = "OUT_STATE_IMAGES";
    public static final String OUT_STATE_POSITION = "OUT_STATE_POSITION";

    private UiLifecycleHelper uiHelper;

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    @InjectView(R.id.login_button)
    LoginButton loginButton;


    BaseStatePagerAdapter<FullScreenPhotoFragment> adapter;
    ArrayList<IFullScreenAvailableObject> photoList = new ArrayList<>();

    TripImagesListFragment.Type type;
    private int position;
    private int lastPage;


    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }


    @Override
    protected TripImagesListPM createPresentationModel(Bundle savedInstanceState) {
        return TripImagesListPM.create(type, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
        outState.putSerializable(OUT_STATE_IMAGES, photoList);
        outState.putSerializable(OUT_STATE_POSITION, pager.getCurrentItem());
    }

    public void shareFBDialog(String url, String text) {
        if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
            FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                    .setLink(url)
                    .setCaption(text)
                    .setApplicationName("DreamTrips")
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else {
            publishFeedDialog(url, text, "DreamTrips");
        }
    }

    private void publishFeedDialog(String picture, String text, String appName) {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            Bundle params = new Bundle();
            params.putString("name", appName);
            params.putString("caption", text);
            params.putString("picture", picture);
            WebDialog feedDialog = (
                    new WebDialog.FeedDialogBuilder(this,
                            Session.getActiveSession(),
                            params))
                    .build();
            feedDialog.setOnCompleteListener((bundle, e) -> {
                if (feedDialog != null) {
                    if (e == null) {
                        informUser(getString(R.string.fab_posted));
                    }
                    feedDialog.dismiss();
                }
            });

            feedDialog.show();
        } else {
            loginButton.setReadPermissions("user_photos");
            loginButton.setSessionStatusCallback((s, state, exception) -> {
                if (session != null && session.isOpened()) {
                    publishFeedDialog(picture, text, appName);
                }
            });
            loginButton.performClick();
        }

    }

    public void shareTwitterDialog(Uri url, String text) {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text(text)
                .image(url);
        builder.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(OUT_STATE_IMAGES);
            int pos = savedInstanceState.getInt(OUT_STATE_POSITION);
            if (serializable != null) {
                photoList = (ArrayList<IFullScreenAvailableObject>) serializable;
                position = pos;
            }
        }
        type = (TripImagesListFragment.Type) bundleExtra.getSerializable(EXTRA_TYPE);
        position = bundleExtra.getInt(EXTRA_POSITION);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        if (position < 0) {
            position = 0;
        }

        if (adapter == null) {
            adapter = new BaseStatePagerAdapter<FullScreenPhotoFragment>(getSupportFragmentManager()) {
                @Override
                public void setArgs(int position, FullScreenPhotoFragment fragment) {
                    Bundle args = new Bundle();
                    args.putInt(FullScreenPhotoFragment.EXTRA_POSITION, position);
                    fragment.setArguments(args);
                }
            };
        }

        toolbar.getBackground().setAlpha(0);

        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                getPresentationModel().scrolled(1, adapter.getCount(), position);
            }
        });
        getPresentationModel().reload(Math.max(position + 2, 15));
    }

    public TripImagesListFragment.Type getType() {
        return type;
    }

    public IFullScreenAvailableObject getPhoto(int position) {
        return photoList.get(position);
    }

    @Override
    public List<IFullScreenAvailableObject> getPhotosFromAdapter() {
        return photoList;
    }

    @Override
    public void startLoading() {

    }

    @Override
    public void finishLoading() {

    }

    @Override
    public void firstLoadFinish() {
        pager.setCurrentItem(position, false);
    }

    @Override
    public void addAll(List<IFullScreenAvailableObject> items) {
        photoList.addAll(items);
        for (IFullScreenAvailableObject item : items) {
            adapter.add(new FragmentItem<>(FullScreenPhotoFragment.class, ""));
        }
        if (items.isEmpty()) {
            lastPage = -1;
        } else {
            lastPage = photoList.size() / TripImagesListPM.PER_PAGE + 1;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void add(IFullScreenAvailableObject item) {

    }

    @Override
    public void add(int position, IFullScreenAvailableObject item) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void replace(int position, IFullScreenAvailableObject item) {

    }

    @Override
    public void remove(int index) {
        if (adapter.getCount() == 1) {
            finish();
        } else {
            int currentItem = pager.getCurrentItem();
            photoList.remove(index);
            adapter.remove(index);
            adapter.notifyDataSetChanged();
            pager.setAdapter(adapter);
            pager.setCurrentItem(Math.min(currentItem, adapter.getCount() - 1));
        }
    }
}
