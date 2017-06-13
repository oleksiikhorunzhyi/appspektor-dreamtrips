package com.worldventures.dreamtrips.wallet.ui.wizard.records.sync;

import android.view.View;

import io.techery.janet.operationsubscriber.view.OperationView;

interface SyncView {

   void setCountPaymentCardsProgress(int syncedCardsCount, int allCardsCount);

   void setProgressInPercent(int percent);

   <T> OperationView<T> provideOperationView();

   void hideProgressOfProcess();

   View getView();
}
