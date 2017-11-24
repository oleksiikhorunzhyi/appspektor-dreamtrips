package com.worldventures.wallet.ui.records.add

import com.worldventures.wallet.domain.bankName
import com.worldventures.wallet.domain.converter.toDomainFinancialService
import com.worldventures.wallet.domain.converter.toDomainRecordType
import com.worldventures.wallet.domain.entity.SDKRecord
import com.worldventures.wallet.domain.entity.record.Record
import com.worldventures.wallet.ui.records.model.RecordViewModel
import com.worldventures.wallet.util.WalletRecordUtil

fun toCreateRecordBundle(record: SDKRecord) = RecordBundle(
      bankName = record.bankName(),
      version = record.version(),
      financialService = record.financialService(),
      cardType = record.cardType(),
      expDate = record.expDate(),
      cvv = record.cvv(),
      cardNumber = record.cardNumber(),
      firstName = record.firstName(),
      middleName = record.middleName(),
      lastName = record.lastName(),
      t1 = record.t1(),
      t2 = record.t2(),
      t3 = record.t3())


fun toRecordViewModel(bundle: RecordBundle) = RecordViewModel(
      cvvLength = WalletRecordUtil.obtainRequiredCvvLength(bundle.cardNumber),
      nickName = "",
      ownerName = WalletRecordUtil.createFullName(bundle.firstName, bundle.middleName, bundle.lastName),
      cardNumber = WalletRecordUtil.obtainLastCardDigits(bundle.cardNumber),
      expireDate = bundle.expDate,
      recordType = bundle.cardType.toDomainRecordType()
)

fun toRecord(bundle: RecordBundle) = Record(
      number = bundle.cardNumber,
      numberLastFourDigits = WalletRecordUtil.obtainLastCardDigits(bundle.cardNumber),
      expDate = bundle.expDate,
      cvv = bundle.cvv,
      track1 = bundle.t1,
      track2 = bundle.t2,
      bankName = bundle.bankName,
      financialService = bundle.financialService.toDomainFinancialService(),
      recordType = bundle.cardType.toDomainRecordType(),
      cardHolderFirstName = bundle.firstName,
      cardHolderMiddleName = bundle.middleName,
      cardHolderLastName = bundle.lastName,
      version = bundle.version
)
