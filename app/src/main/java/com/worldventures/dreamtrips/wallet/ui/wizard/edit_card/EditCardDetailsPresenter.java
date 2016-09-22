package com.worldventures.dreamtrips.wallet.ui.wizard.edit_card;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class EditCardDetailsPresenter extends WalletPresenter<EditCardDetailsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject LocaleHelper localeHelper;
   @Inject SmartCardInteractor smartCardInteractor;

   private final BankCard bankCard;

   public EditCardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
      super(context, injector);
      this.bankCard = bankCard;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      getView().address(ImmutableAddressInfoWithLocale.builder()
            .addressInfo(bankCard.addressInfo())
            .locale(localeHelper.getDefaultLocale())
            .build());
   }

   void onCardInfoConfirmed(AddressInfo addressInfo, boolean useDefaultAddress, boolean setAsDefaultAddress) {
      //TODO implements service layer
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void address(AddressInfoWithLocale defaultAddress);
   }

}
