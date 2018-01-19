package com.worldventures.wallet.ui.records.detail

import com.worldventures.wallet.ui.common.base.WalletPresenter

interface CardDetailsPresenter : WalletPresenter<CardDetailsScreen> {

   fun goBack()

   fun updateNickName()

   fun fetchDefaultRecord()

   fun onDeleteCardClick()

   fun payThisCard()

   fun onChangeDefaultCardConfirmed(recordId: String)

   fun onChangeDefaultCardCanceled()

   fun onDeleteCardConfirmed()

   fun onCardIsReadyDialogShown()

   fun validateRecordName(name: String)

   fun changeDefaultCard(isDefault: Boolean, recordId: String, defaultRecordDetail: DefaultRecordDetail?)
}
