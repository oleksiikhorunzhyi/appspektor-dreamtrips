package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.view.View;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.config.model.TravelBannerRequirement;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.cell_travel_banner)
public class BannerCell extends BaseAbstractDelegateCell<TravelBannerRequirement, BannerCellDelegate> {

   @InjectView(R.id.banner_text) TextView banner;

   public BannerCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      banner.setText(getModelObject().getTitle());
   }

   @OnClick(R.id.banner_text)
   void onBannerClick() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @OnClick(R.id.banner_close)
   void onCloseClicked() {
      cellDelegate.onCancelClicked();
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}

