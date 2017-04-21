package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MembersImagesBasePresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;

import java.util.List;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle.NO_NOTIFICATION;

@Layout(R.layout.fragment_trip_list_images)
public class TripImagesListFragment<T extends TripImagesListPresenter> extends RxBaseFragmentWithArgs<T, TripsImagesBundle>
      implements TripImagesListPresenter.View, SwipeRefreshLayout.OnRefreshListener, MembersImagesBasePresenter.View, SelectablePagerFragment {

   @InjectView(R.id.lv_items) EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipe_container) SwipeRefreshLayout refreshLayout;

   protected BaseDelegateAdapter adapter;
   protected GridLayoutManager layoutManager;
   private RecyclerViewStateDelegate stateDelegate;

   private WeakHandler weakHandler;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      weakHandler = new WeakHandler();
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      stateDelegate.saveStateIfNeeded(outState);
   }

   @Override
   public void onSelectedFromPager() {
      getPresenter().onSelectedFromPager();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      initAdapter();
      initRecyclerView();
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Adapter related
   ///////////////////////////////////////////////////////////////////////////

   private void initAdapter() {
      initLayoutManager(getSpanCount());
      stateDelegate.setRecyclerView(recyclerView);
      adapter = new BaseDelegateAdapter(getContext(), this);
      registerCellsAndDelegates();
      recyclerView.setAdapter(this.adapter);
   }

   protected void initLayoutManager(int spanCount) {
      layoutManager = new GridLayoutManager(getActivity(), spanCount);
      recyclerView.setLayoutManager(layoutManager);
   }

   protected void registerCellsAndDelegates() {
      adapter.registerCell(Photo.class, PhotoCell.class);
      adapter.registerCell(YSBHPhoto.class, PhotoCell.class);
      adapter.registerCell(Inspiration.class, PhotoCell.class);

      CellDelegate<IFullScreenObject> delegate = getPresenter()::onItemClick;
      adapter.registerDelegate(Photo.class, delegate);
      adapter.registerDelegate(YSBHPhoto.class, delegate);
      adapter.registerDelegate(Inspiration.class, delegate);
   }

   protected int getSpanCount() {
      boolean landscape = ViewUtils.isLandscapeOrientation(getActivity());
      return landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
   }

   private void initRecyclerView() {
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int childCount = recyclerView.getChildCount();
            int itemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            getPresenter().scrolled(childCount, itemCount, firstVisibleItemPosition);
         }
      });
   }

   @Override
   public void onDestroyView() {
      stateDelegate.onDestroyView();
      this.recyclerView.setAdapter(null);
      super.onDestroyView();
   }

   @Override
   public void setSelection(int photoPosition) {
      //nothing to do here
   }

   @Override
   public void onRefresh() {
      getPresenter().reload(true);
   }

   @Override
   protected T createPresenter(Bundle savedInstanceState) {
      TripImagesType type = getArgs().getType();
      int userId = getArgs().getUserId();
      return (T) TripImagesListPresenter.create(type, userId, null, 0, NO_NOTIFICATION);
   }

   @Override
   public void startLoading() {
      weakHandler.post(() -> {
         if (refreshLayout != null) refreshLayout.setRefreshing(true);
      });
   }

   @Override
   public void finishLoading() {
      weakHandler.post(() -> {
         if (refreshLayout != null) refreshLayout.setRefreshing(false);
      });
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   public void openFullscreen(FullScreenImagesBundle data) {
      router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(data)
            .build());
   }

   @Override
   public void setImages(List<IFullScreenObject> items) {
      adapter.setItems(items);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void setImages(List<IFullScreenObject> images, UploadingPostsList uploadingPostsList) {
      setImages(images);
   }

   @Override
   public void add(IFullScreenObject item) {
      recyclerView.scrollToPosition(0);
      adapter.addItem(item);
      adapter.notifyItemInserted(adapter.getItemCount() - 1);
   }

   @Override
   public void add(int position, IFullScreenObject item) {
      recyclerView.scrollToPosition(0);
      adapter.addItem(position, item);
      adapter.notifyItemInserted(position);
   }

   @Override
   public void addAll(int position, List<? extends IFullScreenObject> items) {
      recyclerView.scrollToPosition(0);
      adapter.addItems(0, items);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void clear() {
      adapter.clear();
   }

   @Override
   public void replace(int position, IFullScreenObject item) {
      adapter.replaceItem(position, item);
      adapter.notifyItemChanged(position);
   }

   @Override
   public void remove(int index) {
      adapter.remove(index);
      adapter.notifyItemRemoved(index);
   }

   @Override
   public boolean isFullscreenView() {
      return false;
   }

   @Override
   public void openCreatePhoto(MediaAttachment mediaAttachment, CreateEntityBundle.Origin photoOrigin) {
      //TODO temp solution will be removed after refactoring
   }
}
