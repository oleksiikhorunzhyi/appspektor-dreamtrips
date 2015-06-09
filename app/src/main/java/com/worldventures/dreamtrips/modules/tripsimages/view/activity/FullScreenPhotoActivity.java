package com.worldventures.dreamtrips.modules.tripsimages.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.activity_full_screen_photo)
public class FullScreenPhotoActivity extends ActivityWithPresenter<TripImagesListPresenter> implements TripImagesListPresenter.View {
    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String OUT_STATE_IMAGES = "OUT_STATE_IMAGES";
    public static final String OUT_STATE_POSITION = "OUT_STATE_POSITION";

    @InjectView(R.id.pager)
    protected ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    protected BaseStatePagerAdapter adapter;
    protected ArrayList<IFullScreenAvailableObject> photoList = new ArrayList<>();
    protected TripImagesListFragment.Type type;
    private int position;

    @Override
    protected TripImagesListPresenter createPresentationModel(Bundle savedInstanceState) {
        return TripImagesListPresenter.create(type, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(OUT_STATE_IMAGES, photoList);
        outState.putSerializable(OUT_STATE_POSITION, pager.getCurrentItem());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setupAdapter();
        toolbar.getBackground().setAlpha(0);

        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
                //nothing to here
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //nothing to here
            }

            public void onPageSelected(int position) {
                FullScreenPhotoActivity.this.position = position;
                getPresentationModel().scrolled(1, adapter.getCount(), position);
            }
        });
    }

    private void setupAdapter() {
        if (adapter == null) {
            adapter = new BaseStatePagerAdapter(getSupportFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    Bundle args = new Bundle();
                    args.putInt(FullScreenPhotoFragment.EXTRA_POSITION, position);
                    fragment.setArguments(args);
                }

                @Override
                public void addItems(ArrayList baseItemClasses) {

                    photoList.addAll(baseItemClasses);
                    for (Object item : baseItemClasses) {
                        adapter.add(new FragmentItem(FullScreenPhotoFragment.class, ""));
                    }
                }
            };
        }
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
        //nothing to here
    }

    @Override
    public void finishLoading() {
        //nothing to here
    }

    @Override
    public void setSelection() {
        pager.setCurrentItem(position, false);
    }

    @Override
    public IRoboSpiceAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void addAll(List<IFullScreenAvailableObject> items) {
        photoList.addAll(items);
        for (IFullScreenAvailableObject item : items) {
            adapter.add(new FragmentItem(FullScreenPhotoFragment.class, ""));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void add(IFullScreenAvailableObject item) {
        //nothing to here
    }

    @Override
    public void add(int position, IFullScreenAvailableObject item) {
        //nothing to here
    }

    @Override
    public void clear() {
        photoList.clear();
        adapter.clear();
        adapter.notifyDataSetChanged();
        pager.setAdapter(adapter);
    }

    @Override
    public void replace(int position, IFullScreenAvailableObject item) {
        //nothing to here
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
