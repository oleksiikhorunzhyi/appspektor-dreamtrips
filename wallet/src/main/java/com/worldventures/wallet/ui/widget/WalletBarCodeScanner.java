package com.worldventures.wallet.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class WalletBarCodeScanner extends ZXingScannerView {

   private WalletBarCodeFinder barCodeFinder;

   public WalletBarCodeScanner(Context context) {
      this(context, null);
   }

   public WalletBarCodeScanner(Context context, AttributeSet attributeSet) {
      super(context, attributeSet);
   }

   @Override
   protected IViewFinder createViewFinderView(Context context) {
      return new FinderWrapper(context, barCodeFinder);
   }

   public void setBarCodeFinder(WalletBarCodeFinder barCodeFinder) {
      this.barCodeFinder = barCodeFinder;
   }

   private static class FinderWrapper extends View implements IViewFinder {

      private final WalletBarCodeFinder barCodeFinder;

      public FinderWrapper(Context context, WalletBarCodeFinder barCodeFinder) {
         super(context);
         this.barCodeFinder = barCodeFinder;
      }

      @Override
      public void setupViewFinder() {
         barCodeFinder.setupViewFinder();
      }

      @Override
      public Rect getFramingRect() {
         return barCodeFinder.getFramingRect();
      }
   }
}
