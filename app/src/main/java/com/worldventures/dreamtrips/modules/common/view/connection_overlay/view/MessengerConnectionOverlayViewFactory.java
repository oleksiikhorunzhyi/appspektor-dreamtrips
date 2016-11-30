package com.worldventures.dreamtrips.modules.common.view.connection_overlay.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;

public class MessengerConnectionOverlayViewFactory implements ConnectionOverlayViewFactory<MessengerConnectionOverlayView> {

   private Context context;
   private View rootView;
   private int layoutId;

   public MessengerConnectionOverlayViewFactory(Context context, View rootView) {
      this(context, rootView, R.id.content_layout);
   }

   public MessengerConnectionOverlayViewFactory(Context context, View rootView, int layoutId) {
      this.context = context;
      this.rootView = rootView;
      this.layoutId = layoutId;
   }

   @Override
   public MessengerConnectionOverlayView createOverlayView() {
      return new MessengerConnectionOverlayView(context, (ViewGroup) rootView.findViewById(layoutId));
   }
}
