package com.worldventures.dreamtrips.social.ui.bucketlist.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.cell.delegate.BucketPopularCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_popular_cell)
public class BucketPopularCell extends BaseAbstractDelegateCell<PopularBucketItem, BucketPopularCellDelegate> {

   @InjectView(R.id.imageViewImage) protected SimpleDraweeView imageViewImage;
   @InjectView(R.id.textViewName) protected TextView textViewName;
   @InjectView(R.id.textViewDescription) protected TextView textViewDescription;
   @InjectView(R.id.buttonAdd) protected TextView buttonFlatAdd;
   @InjectView(R.id.buttonDone) protected TextView buttonFlatDone;
   @InjectView(R.id.progressBar) protected ProgressBar progressBar;

   public BucketPopularCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      textViewDescription.setText(getModelObject().getDescription());
      textViewName.setText(getModelObject().getName());

      int width = getResources().getDimensionPixelSize(R.dimen.bucket_popular_photo_width);

      imageViewImage.setImageURI(Uri.parse(getModelObject().getCoverPhotoUrl(width, width)));

      if (getModelObject().isLoading()) {
         hideButtons();
      } else {
         showButtons();
      }

   }

   @OnClick(R.id.buttonDone)
   void onDone() {
      cellDelegate.doneClicked(getModelObject(), getLayoutPosition());
      hideButtons();
   }

   @OnClick(R.id.buttonAdd)
   void onAdd() {
      cellDelegate.addClicked(getModelObject(), getLayoutPosition());
      hideButtons();
   }

   private void hideButtons() {
      buttonFlatAdd.setVisibility(View.INVISIBLE);
      buttonFlatDone.setVisibility(View.INVISIBLE);
      progressBar.setVisibility(View.VISIBLE);
   }

   private void showButtons() {
      buttonFlatAdd.setVisibility(View.VISIBLE);
      buttonFlatDone.setVisibility(View.VISIBLE);
      progressBar.setVisibility(View.INVISIBLE);
   }

   @Override
   public boolean shouldInject() {
      return false;
   }

   @Override
   public void prepareForReuse() {
      imageViewImage.setImageBitmap(null);
   }
}
