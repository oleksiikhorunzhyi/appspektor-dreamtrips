package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_list;

import android.content.Context;
import android.view.MenuItem;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.dtl.event.ToggleMerchantSelectionAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableReviewsMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.ReviewObject;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.storage.ReviewStorage;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.views.OfferWithReviewView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionListPresenterImpl extends DtlPresenterImpl<DtlTransactionListScreen, ViewState.EMPTY> implements DtlTransactionListPresenter {

   public DtlTransactionListPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
   }

   @Override
   public void onBackPressed() {

   }
}