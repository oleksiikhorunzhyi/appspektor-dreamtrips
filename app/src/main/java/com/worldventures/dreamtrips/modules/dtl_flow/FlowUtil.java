package com.worldventures.dreamtrips.modules.dtl_flow;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;

import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

import flow.path.Path;

public class FlowUtil {

   private FlowUtil() {
   }

   public static <T extends MasterDetailPath> T currentMaster(View view) {
      return currentMaster(view.getContext());
   }

   public static <T extends MasterDetailPath> T currentMaster(Context context) {
      return (T) ((MasterDetailPath) Path.get(context)).getMaster();
   }

   @LayoutRes
   public static int layoutFrom(Class<? extends Path> pathType) throws IllegalArgumentException {
      Layout layout = pathType.getAnnotation(Layout.class);
      if (layout == null) {
         throw new IllegalArgumentException(String.format("@%s annotation not found on class %s", Layout.class.getSimpleName(), pathType
               .getName()));
      }
      return layout.value();
   }
}
