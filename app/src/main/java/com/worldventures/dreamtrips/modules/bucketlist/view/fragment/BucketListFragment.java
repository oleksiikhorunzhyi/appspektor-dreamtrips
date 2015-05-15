package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
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

import com.gc.materialdesign.widgets.SnackBar;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketHeader;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketHeaderCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CollapsibleAutoCompleteTextView;
import com.worldventures.dreamtrips.modules.common.view.adapter.MyDraggableSwipeableItemAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;
import butterknife.OnClick;

import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType;

@Layout(R.layout.fragment_bucket_list)
@MenuResource(R.menu.menu_bucket)
public class BucketListFragment extends BaseFragment<BucketListPresenter>
        implements BucketListPresenter.View {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";
    public static final int MIN_SYMBOL_COUNT = 3;

    @InjectView(R.id.lv_items)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;

    @InjectView(R.id.textViewEmptyAdd)
    protected TextView textViewEmptyAdd;

    @InjectView(R.id.progressBar)
    protected ProgressBar progressBar;

    private MyDraggableSwipeableItemAdapter<Object> mAdapter;

    private RecyclerViewDragDropManager mDragDropManager;
    private SnackBar snackBar;

    private MenuItem menuItemAdd;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        BucketType type = (BucketType) getArguments().getSerializable(BUNDLE_TYPE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.textViewEmptyAdd.setText(String.format(getString(R.string.bucket_list_add),
                getString(type.getRes())));
        this.recyclerView.setEmptyView(emptyView);

        mDragDropManager = new RecyclerViewDragDropManager();
        mDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z3));

        mAdapter = new MyDraggableSwipeableItemAdapter<>(getActivity(),
                (com.techery.spares.module.Injector) getActivity());
        mAdapter.registerCell(BucketItem.class, BucketItemCell.class);
        mAdapter.registerCell(BucketHeader.class, BucketHeaderCell.class);

        mAdapter.setMoveListener((from, to) -> getPresenter().itemMoved(from, to));

        RecyclerView.Adapter mWrappedAdapter = mDragDropManager.createWrappedAdapter(mAdapter);
        GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        this.recyclerView.setItemAnimator(animator);
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            this.recyclerView.addItemDecoration(
                    new ItemShadowDecorator((NinePatchDrawable) getResources()
                            .getDrawable(R.drawable.material_shadow_z1)));
        }

        this.recyclerView.addItemDecoration(
                new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider),
                        true));
        this.recyclerView.setScrollbarFadingEnabled(false);
        this.recyclerView.setFadingEdgeLength(0);

        mDragDropManager.attachRecyclerView(recyclerView);
    }

    @Override
    public void onDestroyView() {
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuItemAdd = menu.findItem(R.id.action_quick);
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
            String s = v.getText().toString();
            if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(s)) {
                v.setText("");
                getPresenter().addToBucketList(s);
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

    @OnClick(R.id.buttonNew)
    void onAdd() {
        menuItemAdd.expandActionView();
    }

    @OnClick(R.id.buttonPopular)
    void onPopular() {
        getPresenter().addPopular();
    }

    @Override
    public void showUndoBar(View.OnClickListener undoListener) {
        if (snackBar != null && snackBar.isShowing()) {
            snackBar.hide();
        }
        snackBar = new SnackBar(getActivity(), getString(R.string.bucket_delete_undo),
                getString(R.string.undo), undoListener);
        snackBar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                actionFilter();
                break;
            case R.id.action_popular:
                getPresenter().addPopular();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionFilter() {
        View menuItemView = getActivity().findViewById(R.id.action_filter); // SAME ID AS MENU ID

        PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
        popupMenu.inflate(R.menu.menu_bucket_filter);

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

    @Override
    public void showDetailsContainer() {
        getActivity().findViewById(R.id.container_bucket_details).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideContainer() {
        getActivity().onBackPressed();
    }

    @Override
    public void informUser(String stringId) {
        Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.recyclerView.post(() -> getPresenter().loadBucketItems());
    }

    @Override
    public void onPause() {
        mDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    protected BucketListPresenter createPresenter(Bundle savedInstanceState) {
        BucketType type = (BucketType) getArguments().getSerializable(BUNDLE_TYPE);
        return new BucketListPresenter(this, type);
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public boolean detailsOpened() {
        return getActivity().findViewById(R.id.container_bucket_details).getVisibility() == View.VISIBLE;
    }
}