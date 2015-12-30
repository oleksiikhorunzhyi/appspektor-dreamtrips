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
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenPhotoBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.FragmentItemWithObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.SocialViewPagerState;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_full_screen_photo_wrapper)
public class FullScreenPhotoWrapperFragment
        extends BaseFragmentWithArgs<TripImagesListPresenter, FullScreenImagesBundle>
        implements TripImagesListPresenter.View {

    @InjectView(R.id.pager)
    protected ViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    @Inject
    SnappyRepository db;

    protected BaseStatePagerAdapter<FragmentItemWithObject<IFullScreenObject>> adapter;
    protected Route route;

    @Override
    protected TripImagesListPresenter createPresenter(Bundle savedInstanceState) {
        TripImagesType type = getArgs().getType();
        int userId = getArgs().getUserId();
        int position = getArgs().getPosition();
        int notificationId = getArgs().getNotificationId();
        this.route = getArgs().getRoute();
        ArrayList<IFullScreenObject> fixedList = getArgs().getFixedList();
        return TripImagesListPresenter.create(type, userId, fixedList, true, position, notificationId);
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultSocialPagerState();
    }

    private void setupAdapter() {
        adapter = new BaseStatePagerAdapter<FragmentItemWithObject<IFullScreenObject>>(getActivity().getSupportFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
                FullScreenPhotoBundle data = new FullScreenPhotoBundle(fragmentItems.get(position).getObject(), getArgs().getType(), getArgs().isForeign());
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

    protected void setDefaultSocialPagerState() {
        SocialViewPagerState state = new SocialViewPagerState();
        state.setTagHolderVisible(getArgs().getNotificationId() == FullScreenImagesBundle.NO_NOTIFICATION ? false : true);
        state.setContentWrapperVisible(getArgs().getNotificationId() == FullScreenImagesBundle.NO_NOTIFICATION ? true : false);
        db.saveSocialViewPagerState(state);
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
