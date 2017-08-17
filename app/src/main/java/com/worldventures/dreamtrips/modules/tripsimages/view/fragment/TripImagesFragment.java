package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.innahema.collections.query.queriables.Queryable;
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
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseDiffUtilCallback;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.view.cell.delegate.UploadingCellDelegate;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.UploadingPostsSectionCell;
import com.worldventures.dreamtrips.modules.feed.view.fragment.CreateFeedPostFragment;
import com.worldventures.dreamtrips.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.dreamtrips.modules.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.VideoMediaEntity;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.TripImageCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.TripImageTimestampCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.VideoMediaCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.VideoMediaTimestampCell;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_trip_list_images)
public class TripImagesFragment<T extends TripImagesPresenter> extends RxBaseFragmentWithArgs<T, TripImagesArgs>
      implements TripImagesPresenter.View, SelectablePagerFragment {
   public static final int MEDIA_PICKER_ITEMS_COUNT = 15;

   @InjectView(R.id.recyclerView) EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipeLayout) SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.new_images_button) Button newImagesButton;
   @InjectView(R.id.fab_photo) FloatingActionButton addNewPhotoButton;

   protected BaseDelegateAdapter adapter;
   protected GridLayoutManager layoutManager;
   private RecyclerViewStateDelegate stateDelegate;

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
      initAdapter();
      initRecyclerView();
      refreshLayout.setOnRefreshListener(() -> getPresenter().reload());
      refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
   }

   @Override
   public void onDestroyView() {
      stateDelegate.onDestroyView();
      super.onDestroyView();
   }

   @Override
   public void scrollToTop() {
      recyclerView.scrollToPosition(0);
   }

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
      CellDelegate<BaseMediaEntity> delegate = getPresenter()::onItemClick;
      adapter.registerCell(PhotoMediaEntity.class, getArgs().showTimestamps() ? TripImageTimestampCell.class : TripImageCell.class);
      adapter.registerDelegate(PhotoMediaEntity.class, delegate);
      adapter.registerCell(VideoMediaEntity.class, getArgs().showTimestamps() ? VideoMediaTimestampCell.class : VideoMediaCell.class);
      adapter.registerDelegate(VideoMediaEntity.class, delegate);
      adapter.registerCell(UploadingPostsList.class, UploadingPostsSectionCell.class);
      adapter.registerDelegate(UploadingPostsList.class, new UploadingCellDelegate(getPresenter(), getContext()));
   }

   protected int getSpanCount() {
      boolean landscape = ViewUtils.isLandscapeOrientation(getActivity());
      return landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
   }

   private void initRecyclerView() {
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleCount = recyclerView.getChildCount();
            int totalCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            getPresenter().scrolled(visibleCount, totalCount, firstVisibleItemPosition);
         }
      });
   }

   @Override
   public void hideCreateImageButton() {
      addNewPhotoButton.setVisibility(View.GONE);
   }

   @Override
   public void openFullscreen(boolean lastPageReached, int currentItemPosition) {
      router.moveTo(Route.TRIP_IMAGES_FULLSCREEN,
            NavigationConfigBuilder.forActivity()
                  .manualOrientationActivity(true)
                  .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                  .data(TripImagesFullscreenArgs.builder()
                        .tripImagesArgs(getArgs())
                        .lastPageReached(lastPageReached)
                        .currentItemPosition(currentItemPosition)
                        .build())
                  .build());
   }

   @OnClick(R.id.fab_photo)
   public void actionPhoto() {
      getPresenter().addPhotoClicked();
   }

   @Override
   public void openPicker(int durationLimit) {
      MediaPickerDialog mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnDoneListener(getPresenter()::pickedAttachments);
      mediaPickerDialog.show(MEDIA_PICKER_ITEMS_COUNT, durationLimit);
   }

   @Override
   protected T createPresenter(Bundle savedInstanceState) {
      return (T) new TripImagesPresenter(getArgs());
   }

   @Override
   public void updateItems(List items) {
      layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
         @Override
         public int getSpanSize(int position) {
            if (adapter.getItem(position) instanceof UploadingPostsList) {
               return getSpanCount();
            }
            return 1;
         }
      });
      DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BaseDiffUtilCallback(adapter.getItems(), items) {
         @Override
         public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (adapter.getItem(oldItemPosition) instanceof UploadingPostsList && items.get(newItemPosition) instanceof UploadingPostsList){
               return true;
            }
            return super.areItemsTheSame(oldItemPosition, newItemPosition);
         }
      });
      adapter.setItemsNoNotify(items);
      diffResult.dispatchUpdatesTo(adapter);
   }

   @Override
   public void showLoading() {
      refreshLayout.setRefreshing(true);
   }

   @Override
   public void finishLoading() {
      refreshLayout.setRefreshing(false);
   }

   @Override
   public void showNewImagesButton(String newImagesCountString) {
      newImagesButton.setVisibility(View.VISIBLE);
      newImagesButton.setText(newImagesCountString);
   }

   @Override
   public void hideNewImagesButton() {
      newImagesButton.setVisibility(View.GONE);
   }

   @Override
   public void openCreatePhoto(MediaPickerAttachment mediaAttachment) {
      if (isCreatePhotoAlreadyAttached()) return;
      router.moveTo(Route.POST_CREATE, NavigationConfigBuilder.forFragment()
            .backStackEnabled(false)
            .fragmentManager(getActivity().getSupportFragmentManager())
            .containerId(R.id.container_details_floating)
            .data(new CreateEntityBundle(mediaAttachment, getArgs().getOrigin()))
            .build());
   }

   @Override
   public void onSelectedFromPager() {
   }

   private boolean isCreatePhotoAlreadyAttached() {
      return Queryable.from(getActivity().getSupportFragmentManager().getFragments())
            .firstOrDefault(fragment -> fragment instanceof CreateFeedPostFragment) != null;
   }
}
