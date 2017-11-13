package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.listener.ScrollEventListener;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.GetTransactionsCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableTransactionDetailActionParams;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionListPresenterImpl extends DtlPresenterImpl<DtlTransactionListScreen, ViewState.EMPTY> implements DtlTransactionListPresenter {

   private static final int PAGE_SIZE = 15;

   ScrollEventListener listener = new ScrollEventListener() {
      @Override
      public void onScrollBottomReached(int indexOf) {
         loadMoreTransactions(indexOf);
      }
   };

   @Inject MerchantsInteractor merchantsInteractor;
   @State int page;
   @State boolean paginationFinished;
   @State ArrayList<TransactionModel> transactionModels = new ArrayList<>();
   boolean isLoading;

   public DtlTransactionListPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().setEventListener(listener);
      getView().onRefreshProgress();
      merchantsInteractor.getTransactionsPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<GetTransactionsCommand>()
                  .onProgress((command, integer) -> refreshTransactions(command.getCacheData(), true))
                  .onSuccess(command -> refreshTransactions(command.getResult(), false))
                  .onFail((getTransactionsCommand, throwable) -> loadingFinished()));
      merchantsInteractor.getTransactionsPipe().send(GetTransactionsCommand.readCurrentTransactionsCommand());
      if (transactionModels.isEmpty()) {
         loadTransactions();
      }
   }

   private void refreshTransactions(List<TransactionModel> transactions, boolean fromCache) {
      if (!fromCache) {
         processPaginationState(transactions);
      }
      if (page == 0) {
         transactionModels.clear();
         transactionModels.addAll(transactions);
         getView().setAllTransactions(transactions);
      } else {
         transactionModels.addAll(transactions);
         getView().addTransactions(transactions);
      }
   }

   private void processPaginationState(List<TransactionModel> transactionModels) {
      loadingFinished();
      paginationFinished = transactionModels.size() < PAGE_SIZE;
   }

   private void loadingFinished() {
      isLoading = false;
      getView().onRefreshSuccess(false);
      getView().showLoadingFooter(false);
   }

   private void loadTransactions() {
      isLoading = true;
      if (page == 0) {
         getView().onRefreshProgress();
      } else {
         getView().showLoadingFooter(true);
      }
      merchantsInteractor.getTransactionsPipe().send(GetTransactionsCommand.loadFromNetworkCommand(
            ImmutableTransactionDetailActionParams.builder()
                  .localeId(LocaleHelper.getDefaultLocale().getLanguage())
                  .skip(page * PAGE_SIZE)
                  .take(PAGE_SIZE)
                  .excludeInAppPaymentStatusInitiated(DtlMerchantsPresenterImpl.EXCLUDE_INITIADED_TRANSACTIONS)
                  .build()));
   }

   @Override
   public void onBackPressed() {

   }

   @Override
   public void getAllTransactionsToQuery(String query) {
      getView().searchQuery(query);
   }

   public void searchQuery(String query) {
      getView().onRefreshSuccess(true);
      getView().searchQuery(query);
   }

   @Override
   public void loadMoreTransactions(int indexOf) {
      if (isLoading || paginationFinished) {
         return;
      }
      page++;
      loadTransactions();
   }
}
