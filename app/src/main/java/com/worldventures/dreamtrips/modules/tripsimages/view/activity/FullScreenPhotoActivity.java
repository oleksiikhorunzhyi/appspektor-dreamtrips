package com.worldventures.dreamtrips.modules.tripsimages.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment;

import java.util.List;

import butterknife.InjectView;
import icepick.Icicle;

@Layout(R.layout.activity_full_screen_photo)
public class FullScreenPhotoActivity extends ActivityWithPresenter<TripImagesListPresenter>
        implements TripImagesListPresenter.View {
    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    @InjectView(R.id.pager)
    protected ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    protected BaseStatePagerAdapter<FragmentItem> adapter;

    @Icicle
    int position;

    @Override
    protected TripImagesListPresenter createPresentationModel(Bundle savedInstanceState) {
        Bundle bundleExtra = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
        TripImagesListFragment.Type type = (TripImagesListFragment.Type) bundleExtra
                .getSerializable(EXTRA_TYPE);
        position = bundleExtra.getInt(EXTRA_POSITION);
        return TripImagesListPresenter.create(type, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        if (position < 0) {
            position = 0;
        }

        setupAdapter();

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.getBackground().setAlpha(0);
    }

    private void setupAdapter() {
        adapter = new BaseStatePagerAdapter<FragmentItem>(getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                Bundle args = new Bundle();
                args.putSerializable(FullScreenPhotoFragment.EXTRA_TYPE, getIntent()
                        .getBundleExtra(ActivityRouter.EXTRA_BUNDLE)
                        .getSerializable(EXTRA_TYPE));
                args.putSerializable(FullScreenPhotoFragment.EXTRA_PHOTO, getPresentationModel().getPhoto(position));
                fragment.setArguments(args);
            }
        };
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
    public void addAll(List<IFullScreenObject> items) {
        Queryable.from(items).forEachR(item ->
                adapter.add(new FragmentItem(FullScreenPhotoFragment.class, "")));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void add(IFullScreenObject item) {
        //nothing to here
    }

    @Override
    public void add(int position, IFullScreenObject item) {
        //nothing to here
    }

    @Override
    public void clear() {
        //nothing to here
    }

    @Override
    public void replace(int position, IFullScreenObject item) {
        //nothing to here
    }

    @Override
    public void remove(int index) {
        if (adapter.getCount() == 1) {
            finish();
        } else {
            int currentItem = pager.getCurrentItem();
            adapter.remove(index);
            adapter.notifyDataSetChanged();
            pager.setAdapter(adapter);
            pager.setCurrentItem(Math.min(currentItem, adapter.getCount() - 1));
        }
    }
}
