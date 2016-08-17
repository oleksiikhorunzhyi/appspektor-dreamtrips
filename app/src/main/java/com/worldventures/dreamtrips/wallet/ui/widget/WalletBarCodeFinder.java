package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.graphics.Color;

import com.worldventures.dreamtrips.R;

import me.dm7.barcodescanner.core.ViewFinderView;

public class WalletBarCodeFinder extends ViewFinderView {

   public WalletBarCodeFinder(Context context) {
      super(context);
      mBorderPaint.setColor(Color.WHITE);
      mBorderLineLength = context.getResources().getDimensionPixelOffset(R.dimen.wallet_wizard_bar_code_border_size);
   }
}
