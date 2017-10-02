package com.worldventures.dreamtrips.social.ui.feed.view.cell.uploading;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.feed.model.uploading.UploadingPostsList;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedViewInjector;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_uploading_items_section_cell)
public class UploadingPostsSectionCell extends BaseAbstractDelegateCell<UploadingPostsList, UploadingPostsSectionCell.Delegate> {

   @Inject Context context;
   @Inject FeedViewInjector feedViewInjector;

   @InjectView(R.id.card_view_wrapper) CardView cardViewWrapper;
   @InjectView(R.id.uploading_items_container) ViewGroup itemCellsContainer;

   private List<UploadingPostCell> cellsList = new ArrayList<>();

   public UploadingPostsSectionCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      feedViewInjector.initCardViewWrapper(cardViewWrapper);
      List<PostCompoundOperationModel> compoundOperationModels = getModelObject().getPhotoPosts();
      addOrRemoveCells(compoundOperationModels.size());
      for (int i = 0; i < compoundOperationModels.size(); i++) {
         cellsList.get(i).update(compoundOperationModels.get(i), cellDelegate);
      }
   }

   private void addOrRemoveCells(int newSize) {
      int currentCellsSize = cellsList.size();
      for (int i = Math.min(currentCellsSize, newSize); i < Math.max(currentCellsSize, newSize); i++) {
         if (i > currentCellsSize - 1) {
            UploadingPostCell itemCell = new UploadingPostCell(context);
            cellsList.add(itemCell);
            itemCellsContainer.addView(itemCell);
         } else {
            UploadingPostCell sectionCell = cellsList.remove(i);
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
