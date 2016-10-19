package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.ListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.MembersImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoUploadCell;

import java.util.List;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle.NO_NOTIFICATION;

@Layout(R.layout.fragment_trip_list_images)
public class TripImagesListFragment<T extends TripImagesListPresenter> extends RxBaseFragmentWithArgs<T, TripsImagesBundle>
      implements TripImagesListPresenter.View, SwipeRefreshLayout.OnRefreshListener, MembersImagesPresenter.View {

   @InjectView(R.id.lv_items) protected EmptyRecyclerView recyclerView;

   @InjectView(R.id.swipe_container) protected SwipeRefreshLayout refreshLayout;

   private BaseArrayListAdapter arrayListAdapter;
   private LinearLayoutManager layoutManager;

   RecyclerViewStateDelegate stateDelegate;
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
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      this.layoutManager = getLayoutManager();
      this.recyclerView.setLayoutManager(layoutManager);
      stateDelegate.setRecyclerView(recyclerView);

      this.arrayListAdapter = new BaseArrayListAdapter<>(rootView.getContext(), this);
      this.arrayListAdapter.registerCell(Photo.class, PhotoCell.class);
      this.arrayListAdapter.registerCell(YSBHPhoto.class, PhotoCell.class);
      this.arrayListAdapter.registerCell(Inspiration.class, PhotoCell.class);
      this.arrayListAdapter.registerCell(UploadTask.class, PhotoUploadCell.class);
      this.recyclerView.setAdapter(this.arrayListAdapter);

      this.refreshLayout.setOnRefreshListener(this);
      this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

      this.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), (view1, position) -> {
         if (getArgs().getType() == TripImagesType.YOU_SHOULD_BE_HERE)
            TrackingHelper.viewTripImage(TrackingHelper.ACTION_YSHB_IMAGES, getPresenter().getPhoto(position)
                  .getFSId());
         if (getArgs().getType() == TripImagesType.INSPIRE_ME)
            TrackingHelper.viewTripImage(TrackingHelper.ACTION_INSPIRE_ME_IMAGES, getPresenter().getPhoto(position)
                  .getFSId());

         this.getPresenter().onItemClick(position);
      }));
      this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

   private GridLayoutManager getLayoutManager() {
      boolean landscape = ViewUtils.isLandscapeOrientation(getActivity());
      int spanCount = landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
      return new GridLayoutManager(getActivity(), spanCount);
   }

   @Override
   public void onRefresh() {
      getPresenter().reload();
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
   public ListAdapter getAdapter() {
      return arrayListAdapter;
   }

   @Override
   public void openFullscreen(FullScreenImagesBundle data) {
      router.moveTo(Route.FULLSCREEN_PHOTO_LIST, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .data(data)
            .build());
   }

   @Override
   public void fillWithItems(List<IFullScreenObject> items) {
      int itemCount = arrayListAdapter.getItemCount();
      arrayListAdapter.addItems(items);
      arrayListAdapter.notifyItemRangeInserted(itemCount - 1, items.size());
   }

   @Override
   public void add(IFullScreenObject item) {
      recyclerView.scrollToPosition(0);
      arrayListAdapter.addItem(item);
      arrayListAdapter.notifyItemInserted(arrayListAdapter.getItemCount() - 1);
   }

   @Override
   public void add(int position, IFullScreenObject item) {
      recyclerView.scrollToPosition(0);
      arrayListAdapter.addItem(position, item);
      arrayListAdapter.notifyItemInserted(position);
   }

   @Override
   public void addAll(int position, List<? extends IFullScreenObject> items) {
      recyclerView.scrollToPosition(0);
      arrayListAdapter.addItems(0, items);
      arrayListAdapter.notifyDataSetChanged();
   }

   @Override
   public void clear() {
      arrayListAdapter.clear();
   }

   @Override
   public void replace(int position, IFullScreenObject item) {
      arrayListAdapter.replaceItem(position, item);
      arrayListAdapter.notifyItemChanged(position);
   }

   @Override
   public void remove(int index) {
      arrayListAdapter.remove(index);
      arrayListAdapter.notifyItemRemoved(index);
   }

   @Override
   public void openCreatePhoto(MediaAttachment mediaAttachment, CreateEntityBundle.Origin photoOrigin) {
      //TODO temp solution will be removed after refactoring
   }
}
