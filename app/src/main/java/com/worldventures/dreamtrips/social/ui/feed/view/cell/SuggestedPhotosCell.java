package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.model.User;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.profile.view.widgets.SmartAvatarView;
import com.worldventures.dreamtrips.social.ui.feed.presenter.SuggestedPhotoCellPresenterHelper;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.delegate.SuggestedPhotosDelegate;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedViewInjector;
import com.worldventures.dreamtrips.social.ui.feed.view.util.SuggestedPhotosListDecorator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_suggested_photos)
public class SuggestedPhotosCell extends BaseAbstractDelegateCell<SuggestedPhotosCell.SuggestedPhotoModel, SuggestedPhotosDelegate>
      implements CellDelegate<PhotoPickerModel>, SuggestedPhotoCellPresenterHelper.View {

   private static final int OFFSET = 5;
   private static final String SUGGESTION_LIST_STATE_KEY = "suggestion.list.state";

   @Inject @ForActivity Provider<Injector> injectorProvider;
   @Inject FeedViewInjector feedViewInjector;

   @InjectView(R.id.suggestion_avatar) SmartAvatarView avatar;
   @InjectView(R.id.suggested_photos_user) TextView userName;
   @InjectView(R.id.suggested_photos_description) TextView description;
   @InjectView(R.id.suggested_photos) RecyclerView suggestedList;
   @InjectView(R.id.btn_attach) Button btnAttach;
   @InjectView(R.id.card_view_wrapper) CardView cardViewWrapper;

   private BaseDelegateAdapter suggestionAdapter;
   private RecyclerViewStateDelegate stateDelegate;

   public SuggestedPhotosCell(View view) {
      super(view);
      stateDelegate = new RecyclerViewStateDelegate(SUGGESTION_LIST_STATE_KEY); //@see RecyclerViewStateDelegate
   }

   @Override
   protected void syncUIStateWithModel() {
      if (suggestionAdapter == null) {
         stateDelegate = new RecyclerViewStateDelegate();

         final LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
         layoutManager.setAutoMeasureEnabled(true);

         RecyclerView.OnScrollListener preLoadScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);

               int count = layoutManager.getItemCount();
               int position = layoutManager.findLastVisibleItemPosition();

               if (position >= count - OFFSET) {
                  PhotoPickerModel model = (PhotoPickerModel) suggestionAdapter.getItem(layoutManager.getItemCount() - 1);
                  if (model.getDateTaken() < cellDelegate.lastSyncTimestamp()) {
                     cellDelegate.onPreloadSuggestionPhotos(model);
                  }
               }
            }
         };

         suggestionAdapter = new BaseDelegateAdapter(itemView.getContext(), injectorProvider.get());
         suggestionAdapter.registerCell(PhotoPickerModel.class, SuggestionPhotoCell.class);
         suggestionAdapter.registerDelegate(PhotoPickerModel.class, this);

         suggestedList.setLayoutManager(layoutManager);
         suggestedList.setAdapter(suggestionAdapter);
         suggestedList.addItemDecoration(new SuggestedPhotosListDecorator());
         suggestedList.addOnScrollListener(preLoadScrollListener);

         stateDelegate.setRecyclerView(suggestedList);
         cellDelegate.onSuggestionViewCreated(this);
      }

      feedViewInjector.initCardViewWrapper(cardViewWrapper);

      cellDelegate.onSyncViewState();
   }

   @OnClick(R.id.suggestion_cancel)
   void onCancel() {
      cellDelegate.onCancelClicked();
   }

   @OnClick(R.id.btn_attach)
   void onAttach() {
      cellDelegate.onAttachClicked();
   }

   @OnClick(R.id.suggestion_avatar)
   void onAvatarClicked() {
      cellDelegate.onOpenProfileClicked();
   }

   @Override
   public void onCellClicked(PhotoPickerModel model) {
      cellDelegate.onSelectPhoto(model);
      suggestionAdapter.notifyDataSetChanged();
   }

   @Override
   public void appendPhotoSuggestions(List<PhotoPickerModel> items) {
      suggestionAdapter.addItems(items);
   }

   @Override
   public void replacePhotoSuggestions(List<PhotoPickerModel> items) {
      suggestionAdapter.clearAndUpdateItems(items);
   }

   @Override
   public void setUser(User user) {
      avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));
      avatar.setup(user, injectorProvider.get());
      avatar.invalidate();
      //
      userName.setText(user.getFullName());
   }

   @Override
   public void setSuggestionTitle(int sizeOfSelectedPhotos) {
      boolean hasPhotos = sizeOfSelectedPhotos > 0;
      if (hasPhotos) {
         int resource = QuantityHelper.chooseResource(sizeOfSelectedPhotos, R.string.suggested_photo_selected_one, R.string.suggested_photo_selected_multiple);

         description.setText(String.format(itemView.getContext()
               .getResources()
               .getString(resource), sizeOfSelectedPhotos));
      } else {
         description.setText(R.string.suggested_photo);
      }

      btnAttach.setVisibility(hasPhotos ? View.VISIBLE : View.GONE);
   }

   @Override
   public void showMaxSelectionMessage() {
      Snackbar.make(itemView, itemView.getContext()
            .getString(R.string.picker_photo_limit_plural, SuggestedPhotoCellPresenterHelper.MAX_SELECTION_SIZE), Snackbar.LENGTH_SHORT)
            .show();
   }

   @Override
   public void saveInstanceState(@Nullable Bundle bundle) {
      stateDelegate.saveStateIfNeeded(bundle);
   }

   @Override
   public void restoreInstanceState(@Nullable Bundle bundle) {
      stateDelegate.onCreate(bundle);
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   public void notifyListChange() {
      suggestionAdapter.notifyDataSetChanged();
   }

   public static class SuggestedPhotoModel {}
}
