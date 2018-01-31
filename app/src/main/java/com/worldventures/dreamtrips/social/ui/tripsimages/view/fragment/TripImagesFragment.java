package com.worldventures.dreamtrips.social.ui.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.picker.helper.PickerPermissionChecker;
import com.worldventures.core.modules.picker.helper.PickerPermissionUiHandler;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.view.dialog.MediaPickerDialog;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.core.ui.util.permission.PermissionUtils;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseDiffUtilCallback;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.UploadingCellDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.uploading.UploadingPostsSectionCell;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.CreateEntityFragment;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.presenter.TripImagesPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesFullscreenArgs;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.cell.TripImageCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.cell.TripImageTimestampCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.cell.VideoMediaCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.cell.VideoMediaTimestampCell;
import com.worldventures.dreamtrips.social.ui.tripsimages.view.util.GridLayoutManagerPaginationDelegate;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;

@Layout(R.layout.fragment_trip_list_images)
@ComponentPresenter.ComponentTitle(R.string.trip_images)
public class TripImagesFragment<T extends TripImagesPresenter> extends RxBaseFragmentWithArgs<T, TripImagesArgs>
      implements TripImagesPresenter.View, SelectablePagerFragment {

   public static final int VISIBLE_THRESHOLD = 15;
   public static final int MEDIA_PICKER_ITEMS_COUNT = 15;

   @Inject PickerPermissionUiHandler pickerPermissionUiHandler;
   @Inject PermissionUtils permissionUtils;

   @InjectView(R.id.recyclerView) EmptyRecyclerView recyclerView;
   @InjectView(R.id.swipeLayout) SwipeRefreshLayout refreshLayout;
   @InjectView(R.id.new_images_button) Button newImagesButton;
   @InjectView(R.id.fab_photo) FloatingActionButton addNewPhotoButton;

   @State protected int videoDuration;
   @State protected boolean mediaPickerShown;

   protected BaseDelegateAdapter<Object> adapter;
   protected GridLayoutManager layoutManager;
   private RecyclerViewStateDelegate stateDelegate;
   private MediaPickerDialog mediaPickerDialog;

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
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      if (mediaPickerShown) {
         openPicker(videoDuration);
      }
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      initAdapter();
      recyclerView.addOnScrollListener(new GridLayoutManagerPaginationDelegate(getPresenter()::loadNext,
            VISIBLE_THRESHOLD));
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
      recyclerView.smoothScrollToPosition(0);
   }

   private void initAdapter() {
      initLayoutManager(getSpanCount());
      stateDelegate.setRecyclerView(recyclerView);
      adapter = new BaseDelegateAdapter<>(getContext(), this);
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

   @Override
   public void hideCreateImageButton() {
      addNewPhotoButton.setVisibility(View.GONE);
   }

   @Override
   public void openFullscreen(boolean lastPageReached, int currentItemPosition) {
      router.moveTo(TripImagesFullscreenFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
            .manualOrientationActivity(true)
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
   public void showPermissionDenied(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showPermissionDenied(getView());
      }
   }

   @Override
   public void showPermissionExplanationText(String[] permissions) {
      if (permissionUtils.equals(permissions, PickerPermissionChecker.PERMISSIONS)) {
         pickerPermissionUiHandler.showRational(getContext(), answer -> getPresenter().recheckPermission(permissions, answer));
      }
   }

   @Override
   public void openPicker(int durationLimit) {
      mediaPickerShown = true;
      videoDuration = durationLimit;

      mediaPickerDialog = new MediaPickerDialog(getContext());
      mediaPickerDialog.setOnCancelListener(dialogInterface -> mediaPickerShown = false);
      mediaPickerDialog.setOnDoneListener(pickerAttachment -> {
         mediaPickerShown = false;
         getPresenter().pickedAttachments(pickerAttachment);
      });
      mediaPickerDialog.show(MEDIA_PICKER_ITEMS_COUNT, durationLimit);
   }

   @Override
   public void onDestroy() {
      super.onDestroy();
      //fix memory leak after rotation
      if (mediaPickerDialog != null) {
         mediaPickerDialog.dismiss();
      }
   }

   @Override
   protected T createPresenter(Bundle savedInstanceState) {
      return (T) new TripImagesPresenter(getArgs());
   }

   @Override
   public void updateItems(List items, boolean refreshTimeStamp) {
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
            if (adapter.getItem(oldItemPosition) instanceof UploadingPostsList && items.get(newItemPosition) instanceof UploadingPostsList) {
               return true;
            }
            return super.areItemsTheSame(oldItemPosition, newItemPosition);
         }

         @Override
         public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (refreshTimeStamp && adapter.getItem(oldItemPosition) instanceof PhotoMediaEntity
                  && items.get(newItemPosition) instanceof PhotoMediaEntity) {
               return false;
            }
            return super.areContentsTheSame(oldItemPosition, newItemPosition);
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
      if (isCreatePhotoAlreadyAttached()) {
         return;
      }
      router.moveTo(CreateEntityFragment.class, NavigationConfigBuilder.forFragment()
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
            .firstOrDefault(fragment -> fragment instanceof CreateEntityFragment) != null;
   }
}
