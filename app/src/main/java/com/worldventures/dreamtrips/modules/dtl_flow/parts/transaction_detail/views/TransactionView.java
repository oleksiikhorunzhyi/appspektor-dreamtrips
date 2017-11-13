package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.views;

      import android.content.Context;
      import android.support.annotation.Nullable;
      import android.util.AttributeSet;
      import android.widget.LinearLayout;

public class TransactionView extends LinearLayout {

   private Context context;

   public TransactionView(Context context) {
      this(context, null);
      this.context = context;
   }

   public TransactionView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.context = context;
      init();
   }

   private void init() {

   }

}
