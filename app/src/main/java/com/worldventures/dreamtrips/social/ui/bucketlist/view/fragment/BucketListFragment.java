package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

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
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.SoftInputUtil;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.fragment.FragmentHelper;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.common.view.adapter.DraggableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.Suggestion;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.adapter.BucketItemAdapter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.BucketItemStaticCell;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.custom.CollapsibleAutoCompleteTextView;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityDetailsFragment;
import com.worldventures.dreamtrips.util.PopupMenuUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import timber.log.Timber;

@Layout(R.layout.fragment_bucket_list)
@MenuResource(R.menu.menu_bucket)
public class BucketListFragment<T extends BucketListPresenter> extends RxBaseFragment<T>
      implements BucketListPresenter.View, SelectablePagerFragment {

   public static final String BUNDLE_TYPE = "BUNDLE_TYPE";
   public static final int MIN_SYMBOL_COUNT = 3;

   @InjectView(R.id.lv_items) protected EmptyRecyclerView recyclerView;
   @InjectView(R.id.ll_empty_view) protected ViewGroup emptyView;
   @Optional @InjectView(R.id.textViewEmptyAdd) protected TextView textViewEmptyAdd;
   @InjectView(R.id.progressBar) protected ProgressBar progressBar;

   @Optional @InjectView(R.id.detail_container) protected View detailsContainer;

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
         recyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.material_shadow_z1, getActivity()
               .getTheme())));
      }
      recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.list_divider), true));
      recyclerView.setScrollbarFadingEnabled(false);
      recyclerView.setFadingEdgeLength(0);
      // setup empty view
      BucketItem.BucketType type = (BucketItem.BucketType) getArguments().getSerializable(BUNDLE_TYPE);

      if (textViewEmptyAdd != null) {
         textViewEmptyAdd.setText(String.format(getString(R.string.bucket_list_add), getString(type.getRes())));
      }
      recyclerView.setEmptyView(emptyView);
      // setup drag&drop with adapter
      dragDropManager = new RecyclerViewDragDropManager();
      dragDropManager.setInitiateOnLongPress(true);
      dragDropManager.setInitiateOnMove(false);
      dragDropManager.setOnItemDragEventListener(new RecyclerViewDragDropManager.OnItemDragEventListener() {
         @Override
         public void onItemDragStarted(int position) {
            OrientationUtil.lockOrientation(getActivity());
         }

         @Override
         public void onItemDragPositionChanged(int fromPosition, int toPosition) {
            //do nothing
         }

         @Override
         public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
            OrientationUtil.unlockOrientation(getActivity());
         }

         @Override
         public void onItemDragMoveDistanceUpdated(int offsetX, int offsetY) {
            //do nothing
         }
      });
      dragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.material_shadow_z3, getActivity()
            .getTheme()));

      initAdapter();

      wrappedAdapter = dragDropManager.createWrappedAdapter(adapter);
      recyclerView.setAdapter(wrappedAdapter);  // requires *wrapped* adapter
      if (isDragEnabled()) {
         dragDropManager.attachRecyclerView(recyclerView);
      }
      // set state delegate
      stateDelegate.setRecyclerView(recyclerView);
   }

   @Override
   public void onSelectedFromPager() {
      getPresenter().onSelected();
   }

   private void initAdapter() {
      adapter = new BucketItemAdapter(getActivity(), this);
      if (isSwipeEnabled()) {
         adapter.registerCell(BucketItem.class, BucketItemCell.class);
      } else {
         adapter.registerCell(BucketItem.class, BucketItemStaticCell.class);
      }
      adapter.registerDelegate(BucketItem.class, new BucketItemCell.Delegate() {
         @Override
         public void onDoneClicked(BucketItem bucketItem, int position) {
            getPresenter().itemDoneClicked(bucketItem);
         }

         @Override
         public void onCellClicked(BucketItem model) {
            getPresenter().itemClicked(model);
         }
      });
      adapter.setMoveListener((from, to) -> getPresenter().itemMoved(from, to));
   }

   protected boolean isDragEnabled() {
      return true;
   }

   protected boolean isSwipeEnabled() {
      return true;
   }

   @Override
   public void onDestroyView() {
      FragmentHelper.resetChildFragmentManagerField(this);
      stateDelegate.onDestroyView();
      if (dragDropManager != null) {
         try {
            dragDropManager.release();
         } catch (Exception e) {
            //internal NPE in RecyclerViewDragDropManager.java:746
            Timber.e(e, this.getClass().getSimpleName());
         }
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
   protected void onMenuInflated(Menu menu) {
      super.onMenuInflated(menu);
      menuItemAdd = menu.findItem(R.id.action_quick);

      if (menuItemAdd != null) {
         setupQuickTypeInput(menuItemAdd);
      }
   }

   private void setupQuickTypeInput(MenuItem item) {
      View view = MenuItemCompat.getActionView(item);
      CollapsibleAutoCompleteTextView quickInputEditText = (CollapsibleAutoCompleteTextView) view.findViewById(R.id.editTextQuickInput);
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
      adapter.setLoader(getPresenter().suggestionLoader());

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
            getPresenter().onFilterShown();
            actionFilter();
            break;
         case R.id.action_popular:
            getPresenter().popularClicked();
            break;
         default:
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void openPopular(BucketBundle args) {
      router.moveTo(BucketPopularTabsFragment.class, NavigationConfigBuilder.forActivity().data(args).build());
   }

   private void actionFilter() {
      View menuItemView = getActivity().findViewById(R.id.action_filter); // SAME ID AS MENU ID

      PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
      popupMenu.inflate(R.menu.menu_bucket_filter);
      PopupMenuUtils.convertItemsToUpperCase(popupMenu);

      boolean showCompleted = getPresenter().showCompleted;
      boolean showToDO = getPresenter().showToDo;

      if (showCompleted && showToDO) {
         popupMenu.getMenu().getItem(0).setChecked(true);
      } else if (showCompleted) {
         popupMenu.getMenu().getItem(1).setChecked(true);
      } else {
         popupMenu.getMenu().getItem(2).setChecked(true);
      }

      popupMenu.setOnMenuItemClickListener((menuItem) -> {
         getPresenter().reloadWithFilter(menuItem.getItemId());
         return false;
      });

      popupMenu.show();
   }

   WeakHandler handler = new WeakHandler();

   @Override
   public void showDetailsContainer() {
      handler.post(() -> {
         if (detailsContainer != null) {
            detailsContainer.setVisibility(View.VISIBLE);
         }
      });
   }

   @Override
   public void hideDetailContainer() {
      handler.post(() -> {
         if (detailsContainer != null) {
            detailsContainer.setVisibility(View.GONE);
         }
      });
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
      try {
         dragDropManager.cancelDrag();
      } catch (Exception e) {
         //internal NPE in RecyclerViewDragDropManager.java:746
         Timber.e(e, this.getClass().getSimpleName());
      }
      super.onPause();
   }

   @Override
   protected T createPresenter(Bundle savedInstanceState) {
      BucketItem.BucketType type = (BucketItem.BucketType) getArguments().getSerializable(BUNDLE_TYPE);
      return (T) new BucketListPresenter(type);
   }

   @Override
   public void startLoading() {
      if (progressBar != null) {
         progressBar.setVisibility(View.VISIBLE);
      }
   }

   @Override
   public void finishLoading() {
      if (progressBar != null) {
         progressBar.setVisibility(View.GONE);
      }
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   public void setItems(List<? extends BucketItem> items) {
      adapter.setItems(new ArrayList(items));
   }

   @Override
   public void notifyItemsChanged() {
      adapter.notifyDataSetChanged();
   }

   @Override
   public void hideEmptyView() {
      emptyView.setVisibility(View.GONE);
   }

   @Override
   public void openDetails(BucketItem bucketItem) {
      removeDetails();
      FeedEntityDetailsBundle.Builder bundleBuilder = new FeedEntityDetailsBundle.Builder()
            .feedItem(FeedItem.create(bucketItem, bucketItem.getOwner()));
      if (isTabletLandscape()) {
         bundleBuilder.slave(true);
         router.moveTo(FeedEntityDetailsFragment.class, NavigationConfigBuilder.forFragment()
               .backStackEnabled(false)
               .containerId(R.id.detail_container)
               .fragmentManager(getChildFragmentManager())
               .data(bundleBuilder.build())
               .build());
         showDetailsContainer();
      } else {
         hideDetailContainer();
         router.moveTo(FeedEntityDetailsFragment.class, NavigationConfigBuilder.forActivity()
               .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
               .data(bundleBuilder.build())
               .build());
      }
   }

   private void removeDetails() {
      router.moveTo(FeedEntityDetailsFragment.class, NavigationConfigBuilder.forRemoval()
            .fragmentManager(getChildFragmentManager())
            .containerId(R.id.detail_container)
            .build());
   }
}
