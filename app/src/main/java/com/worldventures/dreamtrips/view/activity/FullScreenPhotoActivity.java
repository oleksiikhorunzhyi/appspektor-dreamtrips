package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.presentation.FullScreenActivityPM;
import com.worldventures.dreamtrips.view.adapter.BasePagerAdapter;
import com.worldventures.dreamtrips.view.fragment.FullScreenPhotoFragment;

import java.util.ArrayList;

import butterknife.InjectView;

@Layout(R.layout.activity_full_screen_photo)
public class FullScreenPhotoActivity extends PresentationModelDrivenActivity<FullScreenActivityPM> {
    public static final String EXTRA_PHOTOS_LIST = "EXTRA_PHOTOS_LIST";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    private BasePagerAdapter<FullScreenPhotoFragment> adapter;
    ArrayList<Photo> photoList;

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

        photoList = (ArrayList<Photo>) bundleExtra.getSerializable(EXTRA_PHOTOS_LIST);

        int position = bundleExtra.getInt(EXTRA_POSITION);

        if (position < 0) {
            position = 0;
        }

        adapter = new BasePagerAdapter<FullScreenPhotoFragment>(getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, FullScreenPhotoFragment fragment) {
                Bundle args = new Bundle();
                args.putSerializable(FullScreenPhotoFragment.EXTRA_PHOTO, photoList.get(position));
                fragment.setArguments(args);
            }
        };

        for (Photo photo : photoList) {
            adapter.add(new BasePagerAdapter.FragmentItem<>(FullScreenPhotoFragment.class, ""));
        }

        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        toolbar.getBackground().setAlpha(0);
    }
}
