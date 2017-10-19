package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.views.TransactionView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.verify;

public class DtlTransactionPresenterImplTest {

   private DtlTransactionListPresenterImpl presenter;
   @Mock Context context;
   @Mock Injector injector;
   @Mock DtlTransactionListScreen screen;
   @Mock TransactionView mContainerDetail;

   private List<TransactionModel> mockItems = new ArrayList<>();

   @Before
   public void setUp() throws Exception {
      MockitoAnnotations.initMocks(this);
      generateMockObjects(10);
      presenter = new DtlTransactionListPresenterImpl(context, injector);
      presenter.attachView(screen);
      screen.setTransactionsView(mContainerDetail);
   }

   @Test
   public void itShouldCleanScreenForFirstLoad() {
      presenter.loadFirstPage();
      verify(screen).resetViewData();
   }

   @Test
   public void itShouldShowPageLoader() {
      presenter.loadFirstPage();
      verify(screen).onRefreshProgress();
   }

   @Test
   public void itShouldSearchData() {
      screen.setAllTransactions(getListTransaction());
      presenter.searchQuery("abc");
      verify(screen).searchQuery("abc");
   }

   private void generateMockObjects(int mockObjectCount) {
      for (int i = 0; i < mockObjectCount; i++) {
         TransactionModel transactionModel = new TransactionModel();
         transactionModel.setMerchantName("Merchant 10" + i);
         transactionModel.setSubTotalAmount(500 + new Random().nextInt(500));
         transactionModel.setTax(transactionModel.getSubTotalAmount() * 0.015);
         transactionModel.setTotalAmount(transactionModel.getSubTotalAmount() + transactionModel.getTax());
         transactionModel.setEarnedPoints(1 + new Random().nextInt(4));
         transactionModel.setTip(1 + new Random().nextInt(4));
         transactionModel.setTransactionDate("2017-09-12T14:22:14.000Z UTC");
         transactionModel.setRewardStatus(new Random().nextBoolean());
         mockItems.add(transactionModel);
      }
   }

   private List<TransactionModel> getListTransaction(){
      return mockItems;
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
