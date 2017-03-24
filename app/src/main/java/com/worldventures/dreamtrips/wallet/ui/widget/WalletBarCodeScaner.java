package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class WalletBarCodeScaner extends ZXingScannerView {

   public WalletBarCodeScaner(Context context) {
      super(context);
   }

   public WalletBarCodeScaner(Context context, AttributeSet attributeSet) {
      super(context, attributeSet);
   }

   @Override
   protected IViewFinder createViewFinderView(Context context) {
      return new WalletBarCodeFinder(context, false);
   }
}
