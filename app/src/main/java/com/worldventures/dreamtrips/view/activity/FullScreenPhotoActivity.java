package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;
import com.worldventures.dreamtrips.view.adapter.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.viewpager.FragmentItem;
import com.worldventures.dreamtrips.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.view.fragment.TripImagesListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.activity_full_screen_photo)
public class FullScreenPhotoActivity extends PresentationModelDrivenActivity<TripImagesListPM> implements TripImagesListPM.View {
    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    @InjectView(R.id.pager)
    ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;

    BaseStatePagerAdapter<FullScreenPhotoFragment> adapter;
    List<Object> photoList;
    TripImagesListFragment.Type type;
    private int position;

    @Override
    protected TripImagesListPM createPresentationModel(Bundle savedInstanceState) {
        return TripImagesListPM.create(type, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);

        photoList = new ArrayList<>();
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

        adapter = new BaseStatePagerAdapter<FullScreenPhotoFragment>(getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, FullScreenPhotoFragment fragment) {
                Bundle args = new Bundle();
                args.putInt(FullScreenPhotoFragment.EXTRA_POSITION, position);
                fragment.setArguments(args);
            }
        };

        toolbar.getBackground().setAlpha(0);

        pager.setAdapter(adapter);
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
        getPresentationModel().reload(Math.max(position + 2, 15));
    }

    public TripImagesListFragment.Type getType() {
        return type;
    }

    public Object getPhoto(int position) {
        return photoList.get(position);
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
    public void firstLoadFinish() {
        pager.setCurrentItem(position, false);
    }

    @Override
    public void addAll(List<Object> items) {
        photoList.addAll(items);
        for (Object item : items) {
            adapter.add(new FragmentItem<>(FullScreenPhotoFragment.class, ""));
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
