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
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.FragmentItemWithObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.BucketPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.FullScreenPhotoFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.InspirePhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.SocialImageFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.TripPhotoFullscreenFragment;
import com.worldventures.dreamtrips.modules.tripsimages.view.fragment.temp.YSBHPhotoFullscreenFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;


@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class FullScreenPhotoWrapperFragment
        extends BaseFragmentWithArgs<TripImagesListPresenter, FullScreenImagesBundle>
        implements TripImagesListPresenter.View {

    @InjectView(R.id.pager)
    protected ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    protected BaseStatePagerAdapter<FragmentItemWithObject<IFullScreenObject>> adapter;
    protected Map<TripImagesListFragment.Type, Route> tabTypeMap = new HashMap<>();

    {
        tabTypeMap.put(TripImagesListFragment.Type.INSPIRE_ME, Route.INSPIRE_PHOTO_FULLSCREEN);
        tabTypeMap.put(TripImagesListFragment.Type.MEMBERS_IMAGES, Route.SOCIAL_IMAGE_FULLSCREEN);
        tabTypeMap.put(TripImagesListFragment.Type.ACCOUNT_IMAGES, Route.SOCIAL_IMAGE_FULLSCREEN);
        tabTypeMap.put(TripImagesListFragment.Type.YOU_SHOULD_BE_HERE, Route.YSBH_FULLSCREEN);
        tabTypeMap.put(TripImagesListFragment.Type.TRIP_PHOTO, Route.TRIP_PHOTO_FULLSCREEN);
        tabTypeMap.put(TripImagesListFragment.Type.BUCKET_PHOTO, Route.BUCKET_PHOTO_FULLSCREEN);
        tabTypeMap.put(TripImagesListFragment.Type.FIXED_PHOTO_LIST, Route.SOCIAL_IMAGE_FULLSCREEN);
    }

    @Override
    protected TripImagesListPresenter createPresenter(Bundle savedInstanceState) {
        TripImagesListFragment.Type tab = getArgs().getTab();
        int userId = getArgs().getUserId();
        int position = getArgs().getPosition();
        ArrayList<IFullScreenObject> fixedList = getArgs().getFixedList();
        return TripImagesListPresenter.create(tab, userId, fixedList, true, position);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_photo_back_rounded);
        activity.getSupportActionBar().setTitle("");

        setupAdapter();

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                getPresenter().setCurrentPhotoPosition(position);
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
        adapter = new BaseStatePagerAdapter<FragmentItemWithObject<IFullScreenObject>>(getActivity().getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                FullScreenPhotoBundle data = new FullScreenPhotoBundle(fragmentItems.get(position).getObject(), getArgs().getTab(), getArgs().isForeign());
                ((BaseFragmentWithArgs) fragment).setArgs(data);
            }

            @Override
            public void addItems(ArrayList baseItemClasses) {
                addToAdapter(baseItemClasses);
            }
        };
    }


    @Override
    public void startLoading() {
    }

    @Override
    public void finishLoading() {
    }

    @Override
    public void setSelection(int photoPosition) {
        pager.setCurrentItem(photoPosition, false);
    }

    @Override
    public IRoboSpiceAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void openFullscreen(FullScreenImagesBundle data) {
        NavigationBuilder.create().with(activityRouter)
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .data(data).move(Route.FULLSCREEN_PHOTO_LIST);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void fillWithItems(List<IFullScreenObject> items) {
        addToAdapter(items);
        adapter.notifyDataSetChanged();
    }

    private void addToAdapter(List<IFullScreenObject> items) {
        Queryable.from(items).forEachR(item -> {
            Route route = tabTypeMap.get(getArgs().getTab());
            if (route == null) {
                throw new IllegalStateException("You must specify route for this type");
            } else {
                adapter.add(new FragmentItemWithObject<>(route, "", item));
            }
        });
    }

    @Override
    public void add(IFullScreenObject item) {
    }

    @Override
    public void add(int position, IFullScreenObject item) {
    }

    @Override
    public void clear() {
    }

    @Override
    public void replace(int position, IFullScreenObject item) {
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
