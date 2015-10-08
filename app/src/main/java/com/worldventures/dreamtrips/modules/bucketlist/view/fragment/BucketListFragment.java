package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemClickedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.BucketItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemStaticCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CollapsibleAutoCompleteTextView;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.util.PopupMenuUtils;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;


@Layout(R.layout.fragment_bucket_list)
@MenuResource(R.menu.menu_bucket)
public class BucketListFragment<T extends BucketListPresenter> extends BaseFragment<T>
        implements BucketListPresenter.View {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";
    public static final int MIN_SYMBOL_COUNT = 3;

    @InjectView(R.id.lv_items)
    protected EmptyRecyclerView recyclerView;
    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;
    @Optional
    @InjectView(R.id.textViewEmptyAdd)
    protected TextView textViewEmptyAdd;
    @InjectView(R.id.progressBar)
    protected ProgressBar progressBar;
    //
    @Optional
    @InjectView(R.id.detail_container)
    protected View detailsContainer;

    private DraggableArrayListAdapter<BucketItem> adapter;
    private RecyclerView.Adapter wrappedAdapter;
    private RecyclerViewDragDropManager dragDropManager;
    private RecyclerViewStateDelegate stateDelegate;

    private MenuItem menuItemAdd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        // setup layout manager and item decoration
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new RefactoredDefaultItemAnimator());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addItemDecoration(
                    new ItemShadowDecorator((NinePatchDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.material_shadow_z1, getActivity().getTheme())));
        }
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
        recyclerView.setScrollbarFadingEnabled(false);
        recyclerView.setFadingEdgeLength(0);
        // setup empty view
        BucketItem.BucketType type = (BucketItem.BucketType) getArguments().getSerializable(BUNDLE_TYPE);

        if (textViewEmptyAdd != null)
            textViewEmptyAdd.setText(String.format(getString(R.string.bucket_list_add), getString(type.getRes())));
        recyclerView.setEmptyView(emptyView);
        // setup drag&drop with adapter
        dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.material_shadow_z3, getActivity().getTheme()));
        adapter = new BucketItemAdapter(getActivity(), this);

        if (isSwipeEnabled())
            adapter.registerCell(BucketItem.class, BucketItemCell.class);
        else
            adapter.registerCell(BucketItem.class, BucketItemStaticCell.class);

        adapter.setMoveListener((from, to) -> getPresenter().itemMoved(from, to));
        wrappedAdapter = dragDropManager.createWrappedAdapter(adapter);
        recyclerView.setAdapter(wrappedAdapter);  // requires *wrapped* adapter
        if (isDragEnabled()) dragDropManager.attachRecyclerView(recyclerView);
        // set state delegate
        stateDelegate.setRecyclerView(recyclerView);
    }

    protected boolean isDragEnabled() {
        return true;
    }

    protected boolean isSwipeEnabled() {
        return true;
    }

    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        if (dragDropManager != null) {
            dragDropManager.release();
            dragDropManager = null;
        }
        if (recyclerView != null) {
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(null);
            recyclerView = null;
        }
        if (wrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(wrappedAdapter);
            wrappedAdapter = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuItemAdd = menu.findItem(R.id.action_quick);

        if (menuItemAdd != null)
            setupQuickTypeInput(menuItemAdd);
    }

    private void setupQuickTypeInput(MenuItem item) {
        View view = MenuItemCompat.getActionView(item);
        CollapsibleAutoCompleteTextView quickInputEditText
                = (CollapsibleAutoCompleteTextView) view.findViewById(R.id.editTextQuickInput);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);

        int types = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
        quickInputEditText.setInputType(types);
        quickInputEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        quickInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            String s = v.getText().toString().trim();
            if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(s)) {
                v.setText(null);
                getPresenter().addToBucketList(s);
                SoftInputUtil.showSoftInputMethod(quickInputEditText);
            }
            return false;
        });

        quickInputEditText.setThreshold(MIN_SYMBOL_COUNT);
        AutoCompleteAdapter<Suggestion> adapter = new AutoCompleteAdapter<>(getView().getContext());
        adapter.setLoader(getPresenter().getSuggestionLoader());

        quickInputEditText.setAdapter(adapter);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                quickInputEditText.onActionViewExpanded();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                quickInputEditText.onActionViewCollapsed();
                return true;
            }
        });
    }

    public void onEvent(BucketItemClickedEvent event) {
        getPresenter().itemClicked(event.getBucketItem());
    }

    @Optional
    @OnClick(R.id.buttonNew)
    void onAdd() {
        menuItemAdd.expandActionView();
    }

    @Optional
    @OnClick(R.id.buttonPopular)
    void onPopular() {
        getPresenter().popularClicked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                actionFilter();
                break;
            case R.id.action_popular:
                getPresenter().popularClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openPopular(BucketBundle args) {
        NavigationBuilder.create()
                .with(activityRouter)
                .data(args)
                .move(Route.POPULAR_TAB_BUCKER);
    }

    private void actionFilter() {
        View menuItemView = getActivity().findViewById(R.id.action_filter); // SAME ID AS MENU ID

        PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
        popupMenu.inflate(R.menu.menu_bucket_filter);
        PopupMenuUtils.convertItemsToUpperCase(popupMenu);

        boolean showCompleted = getPresenter().isShowCompleted();
        boolean showToDO = getPresenter().isShowToDO();

        if (showCompleted && showToDO)
            popupMenu.getMenu().getItem(0).setChecked(true);
        else if (showCompleted)
            popupMenu.getMenu().getItem(1).setChecked(true);
        else
            popupMenu.getMenu().getItem(2).setChecked(true);

        popupMenu.setOnMenuItemClickListener((menuItem) -> {
            getPresenter().reloadWithFilter(menuItem.getItemId());
            return false;
        });

        popupMenu.show();
    }

    WeakHandler handler = new WeakHandler();

    @Override
    public void showDetailsContainer() {
        if (detailsContainer != null)
            handler.post(() -> detailsContainer.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideDetailContainer() {
        if (detailsContainer != null)
            handler.post(() -> detailsContainer.setVisibility(View.GONE));
    }

    @Override
    public void putCategoryMarker(int position) {
        adapter.setDragMarker(position, true);
    }

    @Override
    public void informUser(String stringId) {
        Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        dragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    protected T createPresenter(Bundle savedInstanceState) {
        BucketItem.BucketType type = (BucketItem.BucketType) getArguments().getSerializable(BUNDLE_TYPE);
        return (T) new BucketListPresenter(type, getObjectGraph());
    }

    @Override
    public void startLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public BaseArrayListAdapter<BucketItem> getAdapter() {
        return adapter;
    }

    @Override
    public void checkEmpty(int count) {
        if (count != 0) {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void openDetails(BucketItem bucketItem) {
        FeedEntityDetailsBundle bundle = new FeedEntityDetailsBundle(FeedItem.create(bucketItem, bucketItem.getUser()));

        Route detailsRoute = Route.FEED_ENTITY_DETAILS;
        if (isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
            fragmentCompass.setContainerId(R.id.detail_container);
            fragmentCompass.clear();
            bundle.setSlave(true);
            NavigationBuilder.create()
                    .with(fragmentCompass)
                    .data(bundle)
                    .attach(detailsRoute);
            showDetailsContainer();
        } else {
            hideDetailContainer();
            NavigationBuilder.create()
                    .with(activityRouter)
                    .data(bundle)
                    .move(detailsRoute);
        }
    }
}
