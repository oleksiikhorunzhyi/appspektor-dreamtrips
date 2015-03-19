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
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cocosw.undobar.UndoBarController;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.bucket.BucketHeader;
import com.worldventures.dreamtrips.core.model.bucket.BucketItem;
import com.worldventures.dreamtrips.presentation.BucketListFragmentPM;
import com.worldventures.dreamtrips.view.adapter.MyDraggableSwipeableItemAdapter;
import com.worldventures.dreamtrips.view.cell.BucketHeaderCell;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.cell.BucketItemCellOld;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;

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

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    @Global
    EventBus eventBus;

    private BaseArrayListAdapter<Object> mAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BUNDLE_TYPE);
        eventBus.register(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setEmptyView(emptyView);

        mAdapter = new MyDraggableSwipeableItemAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        mAdapter.registerCell(BucketItem.class, BucketItemCell.class);
        mAdapter.registerCell(BucketHeader.class, BucketHeaderCell.class);

        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(mAdapter);  // requires *wrapped* adapter

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)) {
        } else {
            this.recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }
        this.recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));

        this.textViewEmptyAdd.setText(String.format(getString(R.string.bucket_list_add), getString(type.res)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private UndoBarController.UndoBar undoBarController;

    @Override
    public void showUndoBar(UndoBarController.AdvancedUndoListener undoListener) {
        if (undoBarController != null) {
            undoBarController.clear();
        }
        undoBarController = new UndoBarController.UndoBar(getActivity()).message(R.string.delete_photo_text)
                .duration(4000).listener(undoListener);
        undoBarController.show(true);
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
        this.recyclerView.post(() -> {
            getPresentationModel().loadBucketItems(false);
        });
    }

    @Override
    public void onPause() {
        //  mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        getPresentationModel().destroy();
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
        progressBar.setVisibility(View.VISIBLE);
        // recyclerView.post(()->swipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading() {
        progressBar.setVisibility(View.GONE);
        // recyclerView.post(()->swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return mAdapter;
    }
}