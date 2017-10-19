package com.worldventures.dreamtrips.modules.common.view.adapter;


import android.view.View;

import com.worldventures.core.ui.view.cell.AbstractCell;

import butterknife.ButterKnife;

public abstract class BaseAbstractCell<T> extends AbstractCell<T>{

   public BaseAbstractCell(View view) {
      super(view);
      ButterKnife.inject(this, view);
   }
}
