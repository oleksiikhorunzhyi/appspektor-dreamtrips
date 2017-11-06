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
        transactionModel.setEarnedPoints((int)Math.round(safeConversion(detailTransactionThrst.pointsEarned())));
        transactionModel.setTransactionDate(detailTransactionThrst.date());
        transactionModel.setPaymentStatus(mapPaymentStatus(detailTransactionThrst.paymentStatus()));
        return transactionModel;
    }

    private Double safeConversion(Double d) {
        return d == null ? 0 : d;
    }

    private TransactionModel.PaymentStatus mapPaymentStatus(DetailTransactionThrst.PaymentStatus paymentStatus) {
        // mapping server duct tape, it returns null for non thrst transactions
        if (paymentStatus == null) return TransactionModel.PaymentStatus.SUCCESSFUL;

        switch (paymentStatus) {
            case INITIATED:
                return TransactionModel.PaymentStatus.INITIATED;
            case SUCCESSFUL:
                return TransactionModel.PaymentStatus.SUCCESSFUL;
            case UNKNOWN:
            default:
                return TransactionModel.PaymentStatus.UNKNOWN;
        }
    }
}
