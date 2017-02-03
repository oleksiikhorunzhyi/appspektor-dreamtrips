package com.worldventures.dreamtrips.modules.dtl_flow.parts.details;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.Status;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlScreen;

import java.util.List;

public interface DtlDetailsScreen extends DtlScreen {

   void showEstimationDialog(PointsEstimationDialogBundle data);

   void openSuggestMerchant(MerchantIdBundle data);

   void openTransaction(Merchant merchant, DtlTransaction dtlTransaction);

   void showSucceed(Merchant merchant, DtlTransaction dtlTransaction);

   void setTransaction(DtlTransaction dtlTransaction);

   void share(Merchant merchant);

   void locationResolutionRequired(Status status);

   void enableCheckinButton();

   void disableCheckinButton();

   void showMerchantMap(@Nullable Intent intent);

   void setMerchant(Merchant merchant);

   void setupMap();

   void expandOffers(List<String> positions);

   void expandHoursView();

   List<String> getExpandedOffersIds();

   boolean isHoursViewExpanded();

   void showAllReviews();
}
