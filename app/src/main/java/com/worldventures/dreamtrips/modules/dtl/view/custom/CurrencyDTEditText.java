package com.worldventures.dreamtrips.modules.dtl.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.amulyakhare.textdrawable.TextDrawable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;

public class CurrencyDTEditText extends DTEditText {

   public CurrencyDTEditText(Context context) {
      super(context);
   }

   public CurrencyDTEditText(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public CurrencyDTEditText(Context context, AttributeSet attrs, int style) {
      super(context, attrs, style);
   }

   public void setCurrencySymbol(String symbol) {
      final int padding = getPaddingForCurrency(symbol);
      if (symbol.length() > 2) symbol = " " + symbol; //TODO think about fix, very-very dirty

      TextDrawable drawable = TextDrawable.builder()
            .beginConfig()
            .fontSize(getResources().getDimensionPixelSize(R.dimen.font_normal))
            .textColor(getResources().getColor(R.color.black))
            .endConfig()
            .buildRect(symbol, Color.TRANSPARENT);

      setCompoundDrawablePadding(padding);
      setCompoundDrawables(drawable, null, null, null);
   }

   public int getPaddingForCurrency(String symbol) {
      return symbol.length() > 2 ? getResources().getDimensionPixelSize(R.dimen.spacing_large) :
             symbol.length() > 1 ? getResources().getDimensionPixelSize(R.dimen.spacing_normal) :
                  getResources().getDimensionPixelSize(R.dimen.spacing_small);
   }
}
