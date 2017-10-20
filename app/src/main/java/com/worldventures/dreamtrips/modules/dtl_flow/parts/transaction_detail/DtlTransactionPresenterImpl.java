package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.Reviews;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import flow.Flow;

public class DtlTransactionPresenterImpl extends DtlPresenterImpl<DtlTransactionScreen, ViewState.EMPTY> implements DtlTransactionPresenter {
   private TransactionModel transaction;

   public DtlTransactionPresenterImpl(Context context, Injector injector, TransactionModel transaction) {
      super(context);
      injector.inject(this);
      this.transaction = transaction;
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

   @Override
   public void showReceipt() {
      getView().showReceipt(RECEIPT_URL);
   }

   @Override
   public void reviewMerchant() {
         Flow.get(getContext()).set(new DtlCommentReviewPath(merchant));
   }

   //Mock
   private final String RECEIPT_URL = "https://expressexpense.com/images/itemized-receipt.jpg";
   Merchant merchant = new Merchant() {
      @Override
      public String id() {
         return null;
      }

      @Override
      public MerchantType type() {
         return null;
      }

      @Override
      public PartnerStatus partnerStatus() {
         return null;
      }

      @Override
      public String displayName() {
         return null;
      }

      @Nullable
      @Override
      public String address() {
         return null;
      }

      @Nullable
      @Override
      public String city() {
         return null;
      }

      @Nullable
      @Override
      public String state() {
         return null;
      }

      @Nullable
      @Override
      public String country() {
         return null;
      }

      @Nullable
      @Override
      public Coordinates coordinates() {
         return null;
      }

      @Nullable
      @Override
      public String description() {
         return null;
      }

      @Nullable
      @Override
      public Integer budget() {
         return null;
      }

      @Nullable
      @Override
      public Double distance() {
         return null;
      }

      @Nullable
      @Override
      public String zip() {
         return null;
      }

      @Nullable
      @Override
      public Double rating() {
         return null;
      }

      @Nullable
      @Override
      public String phone() {
         return null;
      }

      @Nullable
      @Override
      public String email() {
         return null;
      }

      @Nullable
      @Override
      public String website() {
         return null;
      }

      @Nullable
      @Override
      public List<Currency> currencies() {
         return null;
      }

      @Nullable
      @Override
      public List<Offer> offers() {
         return null;
      }

      @Nullable
      @Override
      public String timeZone() {
         return null;
      }

      @Nullable
      @Override
      public List<ThinAttribute> categories() {
         return null;
      }

      @Nullable
      @Override
      public List<ThinAttribute> amenities() {
         return null;
      }

      @Nullable
      @Override
      public List<MerchantMedia> images() {
         return null;
      }

      @Nullable
      @Override
      public List<OperationDay> operationDays() {
         return null;
      }

      @Nullable
      @Override
      public List<Disclaimer> disclaimers() {
         return null;
      }

      @Nullable
      @Override
      public Reviews reviews() {
         return null;
      }

      @Nullable
      @Override
      public Boolean useThrstFlow() {
         return null;
      }

      @Nullable
      @Override
      public String thrstFullCapabilityUrl() {
         return null;
      }
   };
   //Mock

}