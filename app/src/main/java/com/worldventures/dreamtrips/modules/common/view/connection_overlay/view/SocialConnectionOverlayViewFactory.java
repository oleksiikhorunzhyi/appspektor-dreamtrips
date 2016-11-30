package com.worldventures.dreamtrips.modules.common.view.connection_overlay.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;

public class SocialConnectionOverlayViewFactory implements ConnectionOverlayViewFactory<SocialConnectionOverlayView> {

   private Context context;
   private View rootView;
   private int layoutId;

   public SocialConnectionOverlayViewFactory(Context context, View rootView) {
      this(context, rootView, R.id.content_layout);
   }

   public SocialConnectionOverlayViewFactory(Context context, View rootView, int layoutId) {
      this.context = context;
      this.rootView = rootView;
      this.layoutId = layoutId;
   }

   @Override
   public SocialConnectionOverlayView createOverlayView() {
      return new SocialConnectionOverlayViewImpl(context, (ViewGroup) rootView.findViewById(layoutId));
   }
}
