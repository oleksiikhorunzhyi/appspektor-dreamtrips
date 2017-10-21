package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.common.listener.ScrollEventListener;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.GetTransactionsCommand;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

public class DtlTransactionListPresenterImpl extends DtlPresenterImpl<DtlTransactionListScreen, ViewState.EMPTY> implements DtlTransactionListPresenter {

   ScrollEventListener listener = new ScrollEventListener() {
      @Override
      public void onScrollBottomReached(int indexOf) {
         addMoreTransactions(indexOf);
      }
   };

   @Inject MerchantsInteractor merchantsInteractor;

   public DtlTransactionListPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setEventListener(listener);
      merchantsInteractor.getTransactionsPipe()
            .observeSuccess()
            .map(GetTransactionsCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(detailTransactionThrsts -> {
               //TODO handle transactions here
            });
      merchantsInteractor.getTransactionsPipe().send(GetTransactionsCommand.readCurrentTransactionsCommand());
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
   }

   @Override
   public void onBackPressed() {

   }

   @Override
   public void loadFirstPage() {
      getView().onRefreshProgress();
      getView().resetViewData();
      if (getView().getRunnableView() != null) getView().getRunnableView().postDelayed(new Runnable() {
         @Override
         public void run() {
            try {
               addMoreTransactions(0);
               getView().onRefreshSuccess(false);
            } catch (Exception e) {}
         }
      }, 2000);
   }

   @Override
   public void getAllTransactionsToQuery(String query) {
      if (allItems.isEmpty()) {
         //TODO request all transactions from API
         //Faking loading Time
         getView().onRefreshProgress();
         if (getView().getRunnableView() != null) getView().getRunnableView().postDelayed(new Runnable() {
            @Override
            public void run() {
               try {
                  searchQuery(query);
               } catch (Exception e) {}
            }
         }, 3000);

      } else
         getView().searchQuery(query);
   }

   public void searchQuery(String query) {
      allItems = mockItems;
      getView().setAllTransactions(mockItems);
      getView().onRefreshSuccess(true);
      getView().searchQuery(query);
   }

   @Override
   public void addMoreTransactions(int indexOf) {
      getView().onRefreshSuccess(false);
      getView().addTransactions(getMockObjects(indexOf, 10));
   }

   //Mock
   private List<TransactionModel> allItems = new ArrayList<>();
   private List<TransactionModel> mockItems = new ArrayList<>();

   private void generateMockObjects(int mockObjectCount) {
      for (int i = 0; i < mockObjectCount; i++) {
         TransactionModel transactionModel = new TransactionModel();
         transactionModel.setMerchantName("Merchant 10" + i);
         transactionModel.setSubTotalAmount(500 + new Random().nextInt(500));
         transactionModel.setTax(transactionModel.getSubTotalAmount() * 0.015);
         transactionModel.setTotalAmount(transactionModel.getSubTotalAmount() + transactionModel.getTax());
         transactionModel.setEarnedPoints(1 + new Random().nextInt(4));
         transactionModel.setTip(1 + new Random().nextInt(4));
         transactionModel.setTransactionDate(new Date());
         transactionModel.setRewardStatus(new Random().nextBoolean());
         mockItems.add(transactionModel);
      }
   }

   private List<TransactionModel> getMockObjects(int indexOf, int limit) {
      List<TransactionModel> items = new ArrayList<>();
      if (indexOf >= mockItems.size()) return items;

      int maxLimit = indexOf + limit <= mockItems.size() ? indexOf + limit : mockItems.size();

      for (int i = indexOf; i < maxLimit; i++) {
         items.add(mockItems.get(i));
      }
      return items;
   }
}
