package com.worldventures.dreamtrips.modules.common.view.adapter;

import android.view.View;

import com.worldventures.core.ui.view.cell.AbstractDelegateCell;
import com.worldventures.core.ui.view.cell.CellDelegate;

import butterknife.ButterKnife;


public abstract class BaseAbstractDelegateCell<T, V extends CellDelegate<T>> extends AbstractDelegateCell<T, V> {

   public BaseAbstractDelegateCell(View view) {
      super(view);
      ButterKnife.inject(this, view);
   }
}
