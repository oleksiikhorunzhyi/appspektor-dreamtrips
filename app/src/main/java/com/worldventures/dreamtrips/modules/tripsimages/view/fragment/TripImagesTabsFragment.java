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
import com.kbeanie.imagechooser.api.ChooserType;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.Video360Fragment;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.activity.CreatePhotoActivity;
import com.worldventures.dreamtrips.modules.tripsimages.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icicle;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.BUNDLE_TYPE;
import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

@Layout(R.layout.fragment_trip_images_tabs)
@MenuResource(R.menu.menu_mock)
public class TripImagesTabsFragment extends BaseFragment<TripImagesTabsPresenter>
        implements TripImagesTabsPresenter.View,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener, ViewPager.OnPageChangeListener {

    @InjectView(R.id.tabs)
    protected PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    protected ViewPager pager;
    @InjectView(R.id.v_bg_holder)
    protected View vBgHolder;
    @InjectView(R.id.multiple_actions_down)
    protected FloatingActionsMenu multipleActionsDown;
    @InjectView(R.id.fab_facebook)
    protected FloatingActionButton fabFacebook;
    @InjectView(R.id.fab_gallery)
    protected FloatingActionButton fabGallery;
    @InjectView(R.id.fab_photo)
    protected FloatingActionButton fabPhoto;

    private BaseStatePagerAdapter adapter;
    private PickImageDialog pid;

    @Icicle
    int pidTypeShown;
    @Icicle
    String filePath;


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            this.adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
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
    }

    @Override
    public void setSelection(int selection) {
        pager.setCurrentItem(selection, true);
    }

    @Override
    public void setFabVisibility(boolean facebookGallery) {
        fabFacebook.setVisibility(facebookGallery ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected TripImagesTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new TripImagesTabsPresenter(getArguments());
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
    public void actionFacebook() {
        pidTypeShown = PickImageDialog.REQUEST_FACEBOOK;
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle("");
        this.pid.setCallback(getPresenter().provideCallback(pidTypeShown));
        this.pid.setRequestTypes(PickImageDialog.REQUEST_FACEBOOK);
        this.pid.show();
        filePath = pid.getFilePath();
        this.multipleActionsDown.collapse();
    }

    @OnClick(R.id.fab_gallery)
    public void actionGallery() {
        pidTypeShown = PickImageDialog.REQUEST_PICK_PICTURE;
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle("");
        this.pid.setCallback(getPresenter().provideCallback(pidTypeShown));
        this.pid.setRequestTypes(ChooserType.REQUEST_PICK_PICTURE);
        this.pid.show();
        filePath = pid.getFilePath();
        this.multipleActionsDown.collapse();
    }

    @OnClick(R.id.fab_photo)
    public void actionPhoto() {
        pidTypeShown = PickImageDialog.REQUEST_CAPTURE_PICTURE;
        this.pid = new PickImageDialog(getActivity(), this);
        this.pid.setTitle("");
        this.pid.setCallback(getPresenter().provideCallback(pidTypeShown));
        this.pid.setRequestTypes(ChooserType.REQUEST_CAPTURE_PICTURE);
        this.pid.show();
        filePath = pid.getFilePath();
        this.multipleActionsDown.collapse();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pidTypeShown != 0) {
            this.pid = new PickImageDialog(getActivity(), this);
            this.pid.setCallback(getPresenter().provideCallback(pidTypeShown));
            this.pid.setChooserType(pidTypeShown);
            this.pid.setFilePath(filePath);
            pidTypeShown = 0;
        }
        //
        if (this.pid != null)
            this.pid.onActivityResult(requestCode, resultCode, data);
        //
        if (resultCode == Activity.RESULT_OK && requestCode == CreatePhotoActivity.REQUEST_CODE_CREATE_PHOTO) {
            pager.setCurrentItem(1, false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //nothing to do here
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
        //nothing to do here
    }
}
