package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.Video360Fragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsFragmentPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.BUNDLE_TYPE;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

@Layout(R.layout.fragment_trip_tabs_images)
public class TripImagesTabsFragment extends BaseFragment<TripImagesTabsFragmentPresenter> implements TripImagesTabsFragmentPresenter.View, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener, ViewPager.OnPageChangeListener {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.v_bg_holder)
    View vBgHolder;
    @InjectView(R.id.multiple_actions_down)
    FloatingActionsMenu multipleActionsDown;
    @InjectView(R.id.fab_facebook)
    FloatingActionButton fabFacebook;
    @InjectView(R.id.fab_gallery)
    FloatingActionButton fabGallery;
    @InjectView(R.id.fab_photo)
    FloatingActionButton fabPhoto;

    BasePagerAdapter adapter;
    PickImageDialog pid;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            this.adapter = new BasePagerAdapter(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    if (fragment instanceof TripImagesListFragment) {
                        Bundle args = new Bundle();
                        Type type = Type.values()[position];
                        args.putSerializable(BUNDLE_TYPE, type);
                        fragment.setArguments(args);
                    }
                }
            };

            this.adapter.add(new FragmentItem(TripImagesListFragment.class, getString(R.string.member_images)));
            this.adapter.add(new FragmentItem(TripImagesListFragment.class, getString(R.string.my_images)));
            this.adapter.add(new FragmentItem(TripImagesListFragment.class, getString(R.string.you_should_be_here)));
            this.adapter.add(new FragmentItem(TripImagesListFragment.class, getString(R.string.inspire_me)));
            this.adapter.add(new FragmentItem(Video360Fragment.class, getString(R.string.three_sixty)));

        }
        this.pager.setAdapter(adapter);
        this.tabs.setOnPageChangeListener(this);
        this.tabs.setViewPager(pager);
        this.tabs.setBackgroundColor(getResources().getColor(R.color.theme_main));
        this.multipleActionsDown.setOnFloatingActionsMenuUpdateListener(this);
        getPresenter().onCreate();
    }

    @Override
    public void setFabVisibility(boolean facebookGallery) {
        fabFacebook.setVisibility(facebookGallery ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected TripImagesTabsFragmentPresenter createPresenter(Bundle savedInstanceState) {
        return new TripImagesTabsFragmentPresenter(this);
    }

    @Override
    public void onMenuExpanded() {
        this.vBgHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMenuCollapsed() {
        this.vBgHolder.setVisibility(View.GONE);
    }

    @OnClick(R.id.fab_facebook)
    public void actionFacebook(View view) {
        getPresenter().onFacebookAction(this);
        this.multipleActionsDown.collapse();
    }

    @OnClick(R.id.fab_gallery)
    public void actionGallery(View view) {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle("");
        this.pid.setCallback(getPresenter().providePhotoChooseCallback());
        this.pid.setRequestTypes(ChooserType.REQUEST_PICK_PICTURE);
        this.pid.show();
        this.multipleActionsDown.collapse();
    }

    @OnClick(R.id.fab_photo)
    public void actionPhoto(View view) {
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle("");
        this.pid.setCallback(getPresenter().providePhotoChooseCallback());
        this.pid.setRequestTypes(ChooserType.REQUEST_CAPTURE_PICTURE);
        this.pid.show();
        this.multipleActionsDown.collapse();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pid != null) {
            this.pid.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == FBPickPhotoActivity.REQUEST_CODE_PICK_FB_PHOTO) {
            ChosenImage image = new Gson().fromJson(data.getStringExtra(FBPickPhotoActivity.RESULT_PHOTO), ChosenImage.class);
            getPresenter().provideFbCallback().onResult(this, image, null);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == CreatePhotoActivity.REQUEST_CODE_CREATE_PHOTO) {
            pager.setCurrentItem(1, false);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getPresenter().trackState(position);

        if (position == Type.YOU_SHOULD_BE_HERE.ordinal() || position == Type.INSPIRE_ME.ordinal() || position == Type.VIDEO_360.ordinal()) {
            multipleActionsDown.setVisibility(View.GONE);
        } else {
            multipleActionsDown.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
