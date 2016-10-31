package com.worldventures.dreamtrips.wallet.ui.settings.common.model;

import android.support.annotation.StringRes;

public class SectionDividerModel {

   private @StringRes int titleId;

   public SectionDividerModel(@StringRes int titleId) {
      this.titleId = titleId;
   }

   public int getTitleId() {
      return titleId;
   }
}
