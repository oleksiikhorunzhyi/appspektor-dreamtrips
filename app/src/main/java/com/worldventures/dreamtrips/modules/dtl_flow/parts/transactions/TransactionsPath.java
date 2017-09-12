package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter.TransactionsAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.ArrayList;
import java.util.List;

public class TransactionsPath extends Activity {

   private RecyclerView recyclerViewTransactions;
   private TransactionsAdapter transactionsAdapter;
   private LinearLayoutManager layoutManager;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_transactions);

      recyclerViewTransactions = (RecyclerView) findViewById(R.id.recycler_view);
      layoutManager = new LinearLayoutManager(this);
      recyclerViewTransactions.setLayoutManager(layoutManager);
      recyclerViewTransactions.setHasFixedSize(true);

      List<TransactionModel> transactionList = generateMockedTransactionList(); //Mocked data

      transactionsAdapter = new TransactionsAdapter(transactionList);
      recyclerViewTransactions.setAdapter(transactionsAdapter);
   }

   /** Only for mocking purposes **/
   private  List<TransactionModel> generateMockedTransactionList(){

      List<TransactionModel> tempTransactionList = new ArrayList<>();

      TransactionModel transactionModel = new TransactionModel();
      transactionModel.setMerchantName("Merchant 1");
      transactionModel.setSubTotalAmount("1000");
      transactionModel.setEarnedPoints("1");
      transactionModel.setTransactionDate("Today");
      transactionModel.setTransactionTime("An hour ago");
      transactionModel.setTransactionSuccess(true);
      tempTransactionList.add(0, transactionModel);

      TransactionModel transactionModel1 = new TransactionModel();
      transactionModel1.setMerchantName("Merchant 2");
      transactionModel1.setSubTotalAmount("2000");
      transactionModel1.setEarnedPoints("2");
      transactionModel1.setTransactionDate("Yesterday morning");
      transactionModel1.setTransactionTime("Two hours ago");
      transactionModel1.setTransactionSuccess(false);
      tempTransactionList.add(1, transactionModel1);

      TransactionModel transactionModel2 = new TransactionModel();
      transactionModel2.setMerchantName("Merchant 3");
      transactionModel2.setSubTotalAmount("3000");
      transactionModel2.setEarnedPoints("3");
      transactionModel2.setTransactionDate("Whatever heather");
      transactionModel2.setTransactionTime("3 hours ago");
      transactionModel2.setTransactionSuccess(true);
      tempTransactionList.add(2, transactionModel2);

      TransactionModel transactionModel3 = new TransactionModel();
      transactionModel3.setMerchantName("Merchant 4");
      transactionModel3.setSubTotalAmount("3000");
      transactionModel3.setEarnedPoints("3");
      transactionModel3.setTransactionDate("Whatever heather");
      transactionModel3.setTransactionTime("3 hours ago");
      transactionModel3.setTransactionSuccess(false);
      tempTransactionList.add(3, transactionModel3);

      TransactionModel transactionModel4 = new TransactionModel();
      transactionModel4.setMerchantName("Merchant 5");
      transactionModel4.setSubTotalAmount("3000");
      transactionModel4.setEarnedPoints("3");
      transactionModel4.setTransactionDate("Whatever heather");
      transactionModel4.setTransactionTime("3 hours ago");
      transactionModel4.setTransactionSuccess(true);
      tempTransactionList.add(4, transactionModel4);

      TransactionModel transactionModel5 = new TransactionModel();
      transactionModel5.setMerchantName("Merchant 6");
      transactionModel5.setSubTotalAmount("3000");
      transactionModel5.setEarnedPoints("3");
      transactionModel5.setTransactionDate("Whatever heather");
      transactionModel5.setTransactionTime("3 hours ago");
      transactionModel5.setTransactionSuccess(true);
      tempTransactionList.add(5, transactionModel5);

      TransactionModel transactionModel6 = new TransactionModel();
      transactionModel6.setMerchantName("Merchant 7");
      transactionModel6.setSubTotalAmount("3000");
      transactionModel6.setEarnedPoints("3");
      transactionModel6.setTransactionDate("Whatever heather");
      transactionModel6.setTransactionTime("3 hours ago");
      transactionModel6.setTransactionSuccess(false);
      tempTransactionList.add(6, transactionModel6);

      TransactionModel transactionModel7 = new TransactionModel();
      transactionModel7.setMerchantName("Merchant 8");
      transactionModel7.setSubTotalAmount("3000");
      transactionModel7.setEarnedPoints("3");
      transactionModel7.setTransactionDate("Whatever heather");
      transactionModel7.setTransactionTime("3 hours ago");
      transactionModel7.setTransactionSuccess(false);
      tempTransactionList.add(7, transactionModel7);

      return tempTransactionList;
   }

}
