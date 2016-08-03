package com.worldventures.dreamtrips.wallet.ui.settings.card_details;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.model.AddressInfo;
import com.worldventures.dreamtrips.wallet.model.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import flow.Flow;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

    public CardDetailsPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        AddressInfo addressInfo = ImmutableAddressInfo.builder()
                .address1("12345 Lollipop Drive")
                .address2("Apt 123")
                .city("New York")
                .state("NY")
                .zip("10010")
                .build();

        getView().showDefaultAddress(addressInfo, "USA");
        getView().showCardInfo("Master Card **** 1856");
    }

    public void useDefaultAddressRequired(boolean useDefaultAddress) {
    }

    public void onDeleteCardRequired() {

    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public interface Screen extends WalletScreen {
        void showDefaultAddress(AddressInfo addressInfo, String country);

        void showCardInfo(String cardNumber);
    }

}
