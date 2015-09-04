package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import icepick.Icicle;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class FullScreenPhotoWrapperFragment extends BaseFragment<TripImagesListPresenter> implements TripImagesListPresenter.View {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_FIXED_LIST = "EXTRA_FIXED_LIST";
    public static final String EXTRA_FOREIGN = "EXTRA_FOREIGN";
    public static final String EXTRA_FOREIGN_USER_ID = "EXTRA_FOREIGN_USER_ID";


    @InjectView(R.id.pager)
    protected ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    protected BaseStatePagerAdapter<FragmentItem> adapter;

    @Icicle
    int position;


    @Override
    protected TripImagesListPresenter createPresenter(Bundle savedInstanceState) {
        Bundle args = getArguments();
        TripImagesListFragment.Type type = (TripImagesListFragment.Type) args.getSerializable(EXTRA_TYPE);
        int foreignUserId = args.getInt(EXTRA_FOREIGN_USER_ID);
        position = args.getInt(EXTRA_POSITION);
        ArrayList<IFullScreenObject> fixedList = (ArrayList<IFullScreenObject>) args.getSerializable(EXTRA_FIXED_LIST);
        return TripImagesListPresenter.create(type, true, fixedList, foreignUserId);
    }


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_photo_back_rounded);
        activity.getSupportActionBar().setTitle("");
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
                FullScreenPhotoWrapperFragment.this.position = position;
                getPresenter().scrolled(1, adapter.getCount(), position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.getBackground().setAlpha(0);
    }

    private void setupAdapter() {
        adapter = new BaseStatePagerAdapter<FragmentItem>(getActivity().getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                Bundle args = new Bundle();
                args.putSerializable(FullScreenPhotoFragment.EXTRA_TYPE, getArguments().getSerializable(EXTRA_TYPE));
                args.putSerializable(FullScreenPhotoFragment.EXTRA_PHOTO, getPresenter().getPhoto(position));
                args.putBoolean(EXTRA_FOREIGN, getArguments().getBoolean(EXTRA_FOREIGN));
                fragment.setArguments(args);
            }

            @Override
            public void addItems(ArrayList baseItemClasses) {
                super.addItems(baseItemClasses);
                fill(baseItemClasses);
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
    public void openFullscreen(Bundle args) {
        NavigationBuilder.create().with(activityRouter)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .args(args).move(Route.FULLSCREEN_PHOTO_LIST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void fillWithItems(List<IFullScreenObject> items) {
        fill(items);
    }

    private void fill(List<IFullScreenObject> items) {
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
            getActivity().onBackPressed();
        } else {
            int currentItem = pager.getCurrentItem();
            adapter.remove(index);
            adapter.notifyDataSetChanged();
            pager.setAdapter(adapter);
            pager.setCurrentItem(Math.min(currentItem, adapter.getCount() - 1));
        }
    }
}