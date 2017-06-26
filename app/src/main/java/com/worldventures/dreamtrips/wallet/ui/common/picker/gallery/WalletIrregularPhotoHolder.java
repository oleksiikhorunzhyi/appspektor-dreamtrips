package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter.BaseHolder;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class WalletIrregularPhotoHolder extends BaseHolder<WalletIrregularPhotoModel> {
   @InjectView(R.id.icon) ImageView icon;
   @InjectView(R.id.title) TextView title;

   public WalletIrregularPhotoHolder(View itemView) {
      super(itemView);
      ButterKnife.inject(this, itemView);
   }

   @Override
   public void setData(WalletIrregularPhotoModel data) {
      icon.setImageResource(data.getIconRes());
      title.setText(data.getTitleRes());
      title.setTextColor(ContextCompat.getColor(itemView.getContext(), data.getColorRes()));
   }
}
