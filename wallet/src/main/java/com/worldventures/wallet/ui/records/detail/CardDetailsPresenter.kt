package com.worldventures.wallet.ui.records.detail

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface CardDetailsPresenter : WalletPresenter<CardDetailsScreen> {

   fun goBack()

   fun observeRecordChanges(recordId: String)

   fun updateNickname(recordId: String, nickname: String)

   fun fetchDefaultRecord()

   fun onDeleteCardClick()

   fun payThisCard(recordId: String)

   fun onChangeDefaultCardConfirmed(recordId: String)

   fun onChangeDefaultCardCanceled()

   fun onDeleteCardConfirmed(recordId: String)

   fun onCardIsReadyDialogShown()

   fun validateRecordName(name: String)

   fun changeDefaultCard(isDefault: Boolean, recordId: String, defaultRecordDetail: DefaultRecordDetail?)
}
