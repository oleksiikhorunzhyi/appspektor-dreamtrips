package com.worldventures.dreamtrips.wallet.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class WalletBarCodeScanner extends ZXingScannerView {

   public WalletBarCodeScanner(Context context) {
      super(context);
   }

   public WalletBarCodeScanner(Context context, AttributeSet attributeSet) {
      super(context, attributeSet);
   }

   @Override
   protected IViewFinder createViewFinderView(Context context) {
      return new WalletBarCodeFinder(context, false);
   }
}
