package com.worldventures.dreamtrips.modules.tripsimages.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.SmartAvatarView;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Image;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import butterknife.Optional;

@Layout(R.layout.adapter_item_photo)
public class PhotoCell extends AbstractDelegateCell<IFullScreenObject, CellDelegate<IFullScreenObject>> {

   @InjectView(R.id.imageViewPhoto) SimpleDraweeView draweeViewPhoto;
   @Optional @InjectView(R.id.user_photo) SmartAvatarView imageViewUser;
   @Optional @InjectView(R.id.user_location) TextView userLocation;
   @Optional @InjectView(R.id.user_name) TextView userName;
   @Optional @InjectView(R.id.title) TextView title;
   @Optional @InjectView(R.id.shot_location) TextView shotLocation;

   @Inject @ForActivity Provider<Injector> injectorProvider;

   public PhotoCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      if (imageViewUser != null) {
         User user = getModelObject().getUser();
         this.userLocation.setText(user.getLocation());
         this.shotLocation.setText(getModelObject().getFSLocation());
         this.title.setText(getModelObject().getFSTitle());
         this.userName.setText(user.getFullName());
         imageViewUser.setup(getModelObject().getUser(), injectorProvider.get());
      }

      Image fsImage = getModelObject().getFSImage();
      draweeViewPhoto.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0F));

      if (fsImage.isFromFile()) {
         draweeViewPhoto.setImageURI(Uri.parse(fsImage.getUrl()));
      } else {
         draweeViewPhoto.setImageURI(Uri.parse(fsImage.getThumbUrl(itemView.getResources())));
      }

      itemView.setOnClickListener(v -> cellDelegate.onCellClicked(getModelObject()));
   }

   @Override
   public void prepareForReuse() {
      this.draweeViewPhoto.setImageURI(Uri.EMPTY);
   }
}
