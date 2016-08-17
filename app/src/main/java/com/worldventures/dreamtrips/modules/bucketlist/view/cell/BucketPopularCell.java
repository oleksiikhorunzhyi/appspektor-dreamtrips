package com.worldventures.dreamtrips.modules.bucketlist.view.cell;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.delegate.BucketPopularCellDelegate;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_popular_cell)
public class BucketPopularCell extends AbstractDelegateCell<PopularBucketItem, BucketPopularCellDelegate> {

   @InjectView(R.id.imageViewImage) protected SimpleDraweeView imageViewImage;
   @InjectView(R.id.textViewName) protected TextView textViewName;
   @InjectView(R.id.textViewDescription) protected TextView textViewDescription;
   @InjectView(R.id.buttonAdd) protected TextView buttonFlatAdd;
   @InjectView(R.id.buttonDone) protected TextView buttonFlatDone;
   @InjectView(R.id.progressBar) protected ProgressBar progressBar;

   @Inject protected Context context;

   public BucketPopularCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      textViewDescription.setText(getModelObject().getDescription());
      textViewName.setText(getModelObject().getName());

      int width = context.getResources().getDimensionPixelSize(R.dimen.bucket_popular_photo_width);

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
   public void prepareForReuse() {
      imageViewImage.setImageBitmap(null);
   }
}
