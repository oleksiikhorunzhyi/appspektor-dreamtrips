package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;
import com.worldventures.dreamtrips.view.adapter.BasePagerAdapter;
import com.worldventures.dreamtrips.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.activity_full_screen_photo)
public class FullScreenPhotoActivity extends PresentationModelDrivenActivity<TripImagesListPM> implements TripImagesListPM.View {
    public static final String EXTRA_PHOTOS_LIST = "EXTRA_PHOTOS_LIST";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    BasePagerAdapter<FullScreenPhotoFragment> adapter;
    List<Object> photoList;
    TripImagesListFragment.Type type;

    @Override
    protected TripImagesListPM createPresentationModel(Bundle savedInstanceState) {
        return TripImagesListPM.create(type, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);

        photoList = (ArrayList<Object>) bundleExtra.getSerializable(EXTRA_PHOTOS_LIST);
        type = (TripImagesListFragment.Type) bundleExtra.getSerializable(EXTRA_TYPE);
        int position = bundleExtra.getInt(EXTRA_POSITION);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        getIntent().removeExtra(EXTRA_PHOTOS_LIST);
        if (position < 0) {
            position = 0;
        }

        adapter = new BasePagerAdapter<FullScreenPhotoFragment>(getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, FullScreenPhotoFragment fragment) {
                Bundle args = new Bundle();
                args.putInt(FullScreenPhotoFragment.EXTRA_POSITION, position);
                fragment.setArguments(args);
            }
        };

        for (Object photo : photoList) {
            adapter.add(new BasePagerAdapter.FragmentItem<>(FullScreenPhotoFragment.class, ""));
        }

        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        toolbar.getBackground().setAlpha(0);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (adapter.getCount() - 2 == position) {
                    getPresentationModel().loadNext(photoList.size() / TripImagesListPM.PER_PAGE + 1);
                }

            }
        });
    }

    public TripImagesListFragment.Type getType() {
        return type;
    }

    public Photo getPhoto(int position) {
        return (Photo) photoList.get(position);
    }

    @Override
    public List<Object> getPhotosFromAdapter() {
        return photoList;
    }

    @Override
    public void startLoading() {

    }

    @Override
    public void finishLoading() {

    }

    @Override
    public void addAll(List<Object> items) {
        photoList.addAll(items);
        for (Object item : items) {
            adapter.add(new BasePagerAdapter.FragmentItem<>(FullScreenPhotoFragment.class, ""));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void add(Object item) {

    }

    @Override
    public void add(int position, Object item) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void replace(int position, Object item) {

    }
}
