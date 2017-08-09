package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.custom;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.RestoreViewOnCreateController;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;

public class DisabledTrackingController extends RestoreViewOnCreateController {

   @NonNull
   @Override
   protected View onCreateView(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup, @Nullable Bundle bundle) {
      final View view = layoutInflater.inflate(R.layout.empty_location_lost_card, viewGroup, false);
      final TextView tvDisableLostCardMsg = (TextView) view.findViewById(R.id.tv_empty_lost_card_msg);
      tvDisableLostCardMsg.setText(ProjectTextUtils.fromHtml(getResources().getString(R.string.wallet_lost_card_empty_view)));
      return view;
   }

   @Override
   public boolean handleBack() {
      return false;
   }
}
