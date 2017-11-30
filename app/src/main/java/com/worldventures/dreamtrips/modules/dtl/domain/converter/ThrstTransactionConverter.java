package com.worldventures.dreamtrips.modules.dtl.domain.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions.DetailTransactionThrst;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import io.techery.mappery.MapperyContext;

public class ThrstTransactionConverter implements Converter<DetailTransactionThrst, TransactionModel> {

   @Override
   public Class<DetailTransactionThrst> sourceClass() {
      return DetailTransactionThrst.class;
   }

   @Override
   public Class<TransactionModel> targetClass() {
      return TransactionModel.class;
   }

   @Override
   public TransactionModel convert(MapperyContext mapperyContext, DetailTransactionThrst detailTransactionThrst) {
      TransactionModel transactionModel = new TransactionModel();
      transactionModel.setId(detailTransactionThrst.id());
      transactionModel.setMerchantId(detailTransactionThrst.merchantId());
      transactionModel.setMerchantName(detailTransactionThrst.merchantName());
      transactionModel.setReceiptUrl(detailTransactionThrst.receiptUrl());
      transactionModel.setSubTotalAmount(safeConversion(detailTransactionThrst.subTotalAmount()));
      transactionModel.setTotalAmount(safeConversion(detailTransactionThrst.totalAmount()));
      transactionModel.setTax(safeConversion(detailTransactionThrst.tax()));
      transactionModel.setTip(safeConversion(detailTransactionThrst.tip()));
      transactionModel.setEarnedPoints((int) Math.round(safeConversion(detailTransactionThrst.pointsEarned())));
      transactionModel.setTransactionDate(detailTransactionThrst.date());
      transactionModel.setThrstPaymentStatus(mapPaymentStatus(detailTransactionThrst.paymentStatus()));
      transactionModel.setTrhstTransaction(detailTransactionThrst.isThrstTransaction());
      return transactionModel;
   }

   private Double safeConversion(Double d) {
      return d == null ? 0 : d;
   }

   private TransactionModel.ThrstPaymentStatus mapPaymentStatus(DetailTransactionThrst.PaymentStatus paymentStatus) {
      if (paymentStatus == null) {
         return TransactionModel.ThrstPaymentStatus.UNKNOWN;
      }

      switch (paymentStatus) {
         case INITIATED:
            return TransactionModel.ThrstPaymentStatus.INITIATED;
         case SUCCESSFUL:
            return TransactionModel.ThrstPaymentStatus.SUCCESSFUL;
         case REFUNDED:
            return TransactionModel.ThrstPaymentStatus.REFUNDED;
         case UNKNOWN:
         default:
            return TransactionModel.ThrstPaymentStatus.UNKNOWN;
      }
   }
}
