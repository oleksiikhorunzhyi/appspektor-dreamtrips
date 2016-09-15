package com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image;

import android.content.Context;

import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;

public class DtlFullscreenImagePresenterImpl extends DtlPresenterImpl<DtlFullscreenImageScreen, ViewState.EMPTY> implements DtlFullscreenImagePresenter {

   private String imageUrl;

   public DtlFullscreenImagePresenterImpl(Context context, String imageUrl) {
      super(context);
      this.imageUrl = imageUrl;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().showImage(imageUrl);
   }
}
