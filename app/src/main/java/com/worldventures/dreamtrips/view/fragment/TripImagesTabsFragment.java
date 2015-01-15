package com.worldventures.dreamtrips.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kbeanie.imagechooser.api.ChooserType;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.adapter.BasePagerAdapter;
import com.worldventures.dreamtrips.view.dialog.PickImageDialog;
import com.worldventures.dreamtrips.view.dialog.PickImageFacebookDialog;
import com.worldventures.dreamtrips.presentation.TripImagesTabsFragmentPresentation;

import org.robobinding.ViewBinder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.BUNDLE_TYPE;
import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.Type;

@Layout(R.layout.fragment_trip_tabs_images)
public class TripImagesTabsFragment extends BaseFragment<TripImagesTabsFragmentPresentation> implements TripImagesTabsFragmentPresentation.View, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

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

    TripImagesTabsFragmentPresentation pm;
    BasePagerAdapter adapter;
    PickImageDialog pid;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        adapter = new BasePagerAdapter(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                Bundle args = new Bundle();
                Type type = position == 0 ? Type.MEMBER_IMAGES : position == 1 ? Type.MY_IMAGES : Type.YOU_SHOULD_BE_HERE;
                args.putSerializable(BUNDLE_TYPE, type);
                fragment.setArguments(args);
            }
        };

        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, getString(R.string.member_images)));
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, getString(R.string.my_images)));
        adapter.add(new BasePagerAdapter.FragmentItem(TripImagesListFragment.class, getString(R.string.you_should_be_here)));

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        multipleActionsDown.setOnFloatingActionsMenuUpdateListener(this);
    }

    @Override
    protected TripImagesTabsFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        return new TripImagesTabsFragmentPresentation(this);
    }

    @Override
    public void onMenuExpanded() {
        vBgHolder.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
    }

    @Override
    public void onMenuCollapsed() {
        vBgHolder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @OnClick(R.id.fab_facebook)
    public void actionFacebook(View view) {
        PickImageFacebookDialog dialog = new PickImageFacebookDialog(this, getFragmentManager());
        dialog.setCallback(pm.provideFbCallback());
        dialog.show();
        multipleActionsDown.collapse();
    }

    @OnClick(R.id.fab_gallery)
    public void actionGallery(View view) {
        pid = new PickImageDialog(getActivity(), this);
        pid.setTitle("Select avatar");
        pid.setCallback(pm.providePhotoChooseCallback());
        pid.setRequestTypes(ChooserType.REQUEST_PICK_PICTURE);
        pid.show();
        multipleActionsDown.collapse();
    }

    @OnClick(R.id.fab_photo)
    public void actionPhoto(View view) {
        pid = new PickImageDialog(getActivity(), this);
        pid.setTitle("Select avatar");
        pid.setCallback(pm.providePhotoChooseCallback());
        pid.setRequestTypes(ChooserType.REQUEST_CAPTURE_PICTURE);
        pid.show();
        multipleActionsDown.collapse();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pid.onActivityResult(requestCode, resultCode, data);
    }
}
