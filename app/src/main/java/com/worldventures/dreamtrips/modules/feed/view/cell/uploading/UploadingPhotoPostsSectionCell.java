package com.worldventures.dreamtrips.modules.feed.view.cell.uploading;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.modules.feed.view.cell.uploading.util.PostCompoundOperationModelComparator;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.FeedViewInjector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_uploading_items_section_cell)
public class UploadingPhotoPostsSectionCell extends AbstractDelegateCell<UploadingPostsList, UploadingPhotoPostsSectionCell.Delegate> {

   private static final Comparator<CompoundOperationModel> CELLS_COMPARATOR = new PostCompoundOperationModelComparator();

   @Inject Context context;
   @Inject FeedViewInjector feedViewInjector;

   @InjectView(R.id.card_view_wrapper) CardView cardViewWrapper;
   @InjectView(R.id.uploading_items_container) ViewGroup itemCellsContainer;

   private List<UploadingPhotoPostCell> cellsList = new ArrayList<>();

   public UploadingPhotoPostsSectionCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      feedViewInjector.initCardViewWrapper(cardViewWrapper);
      List<PostCompoundOperationModel> compoundOperationModels = getModelObject().getPhotoPosts();
      addOrRemoveCells(compoundOperationModels.size());
      Collections.sort(compoundOperationModels, CELLS_COMPARATOR);
      for (int i = 0; i < compoundOperationModels.size(); i++) {
         cellsList.get(i).update(compoundOperationModels.get(i), cellDelegate);
      }
   }

   private void addOrRemoveCells(int newSize) {
      int currentCellsSize = cellsList.size();
      for (int i = Math.min(currentCellsSize, newSize); i < Math.max(currentCellsSize, newSize); i++) {
         if (i > currentCellsSize - 1) {
            UploadingPhotoPostCell itemCell = new UploadingPhotoPostCell(context);
            cellsList.add(itemCell);
            itemCellsContainer.addView(itemCell);
         } else {
            UploadingPhotoPostCell sectionCell = cellsList.remove(i);
            itemCellsContainer.removeView(sectionCell);
         }
      }
   }

   public interface Delegate extends CellDelegate<UploadingPostsList> {

      void onUploadPauseClicked(PostCompoundOperationModel model);

      void onUploadResumeClicked(PostCompoundOperationModel model);

      void onUploadRetryClicked(PostCompoundOperationModel model);

      void onUploadCancelClicked(PostCompoundOperationModel model);
   }
}
