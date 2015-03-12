package com.worldventures.dreamtrips.view.fragment;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.presentation.BucketListFragmentPM;
import com.worldventures.dreamtrips.view.adapter.MyDraggableSwipeableItemAdapter;
import com.worldventures.dreamtrips.view.cell.BucketHeaderCell;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.view.util.SwipingActionGuardManager;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@Layout(R.layout.fragment_bucket_list)
@MenuResource(R.menu.menu_bucket)
public class BucketListFragment extends BaseFragment<BucketListFragmentPM> implements BucketListFragmentPM.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";

    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    @InjectView(R.id.textViewEmptyAdd)
    TextView textViewEmptyAdd;

    @Inject
    @Global
    EventBus eventBus;

    private MyDraggableSwipeableItemAdapter<Object> mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private SwipingActionGuardManager mRecyclerViewTouchActionGuardManager;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BUNDLE_TYPE);
        eventBus.register(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setEmptyView(emptyView);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3));

        mAdapter = new MyDraggableSwipeableItemAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        mAdapter.registerCell(BucketItem.class, BucketItemCell.class);
        mAdapter.registerCell(BucketHeader.class, BucketHeaderCell.class);

        mAdapter.setMoveListener((from, to) -> getPresentationModel().itemMoved(from, to));

        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mAdapter);      // wrap for dragging

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        this.recyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            this.recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }
        this.recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));

        //   mRecyclerViewSwipeManager.attachRecyclerView(this.recyclerView);
        mRecyclerViewDragDropManager.attachRecyclerView(this.recyclerView);

        this.textViewEmptyAdd.setText(String.format(getString(R.string.bucket_list_add), getString(type.res)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().setProgressBarVisibility(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                View menuItemView = getActivity().findViewById(R.id.action_filter); // SAME ID AS MENU ID
                PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
                popupMenu.inflate(R.menu.menu_bucket_filter);
                popupMenu.setOnMenuItemClickListener((menuItem) -> {

                    getPresentationModel().reloadWithFilter(menuItem.getItemId());

                    return false;
                });
                popupMenu.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.mAdapter.getItemCount() == 0) {
            this.recyclerView.post(() -> {
                getPresentationModel().loadBucketItems(false);
            });
        }
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager.release();
            mRecyclerViewTouchActionGuardManager = null;
        }

        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;

        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        getPresentationModel().loadBucketItems(true);
    }

    @Override
    protected BucketListFragmentPM createPresentationModel(Bundle savedInstanceState) {
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BUNDLE_TYPE);
        return new BucketListFragmentPM(this, type);
    }

    @Override
    public void startLoading() {
       // recyclerView.post(()->swipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading() {
       // recyclerView.post(()->swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return mAdapter;
    }
}