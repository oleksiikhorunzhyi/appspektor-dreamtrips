package com.worldventures.dreamtrips.modules.tripsimages.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.fullscreen.FullScreenActivityPM;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.DetailedImagePagerFragment;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.InjectView;

@Layout(R.layout.activity_full_screen_photo)
public class FullScreenTripImageActivity extends ActivityWithPresenter<FullScreenActivityPM> {
    public static final String EXTRA_PHOTOS_LIST = "EXTRA_PHOTOS_LIST";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    ArrayList<Serializable> photoList;
    private BaseStatePagerAdapter<DetailedImagePagerFragment> adapter;

    @Override
    protected FullScreenActivityPM createPresentationModel(Bundle savedInstanceState) {
        return new FullScreenActivityPM(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);

        photoList = (ArrayList<Serializable>) bundleExtra.getSerializable(EXTRA_PHOTOS_LIST);

        int position = bundleExtra.getInt(EXTRA_POSITION);

        if (position < 0) {
            position = 0;
        }

        adapter = new BaseStatePagerAdapter<DetailedImagePagerFragment>(getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, DetailedImagePagerFragment fragment) {
                Bundle args = new Bundle();
                args.putSerializable(DetailedImagePagerFragment.EXTRA_PHOTO, photoList.get(position));
                fragment.setArguments(args);
            }
        };

        for (Serializable photo : photoList) {
            adapter.add(new FragmentItem<>(DetailedImagePagerFragment.class, ""));
        }

        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        toolbar.getBackground().setAlpha(0);
    }
}
