package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.trips.model.filter.BoolFilter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public abstract class BoolCell<T extends BoolFilter, D extends CellDelegate<T>> extends BaseAbstractDelegateCell<T, D> {

   @InjectView(R.id.checkFavorites) CheckBox checkFavorites;
   @InjectView(R.id.textViewFavorite) TextView title;

   public BoolCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      title.setText(getTitle());
      checkFavorites.setChecked(getModelObject().isActive());
      checkFavorites.setOnCheckedChangeListener((buttonView, isChecked) -> {
         getModelObject().setActive(isChecked);
         sendEvent(isChecked);
      });
   }

   @OnClick(R.id.textViewFavorite)
   void checkBoxTextViewClicked() {
      checkFavorites.setChecked(!checkFavorites.isChecked());
      getModelObject().setActive(checkFavorites.isChecked());
   }

   public abstract int getTitle();


   public abstract void sendEvent(boolean b);
}
