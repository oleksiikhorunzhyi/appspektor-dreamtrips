package com.worldventures.dreamtrips.wallet.ui.settings.card_details;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableAddressInfoWithLocale;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.smartcard.model.Record;

public class CardDetailsPresenter extends WalletPresenter<CardDetailsPresenter.Screen, Parcelable> {

    @Inject
    SessionHolder<UserSession> userSessionHolder;
    @Inject
    LocaleHelper localeHelper;

    private final BankCard bankCard;

    public CardDetailsPresenter(Context context, Injector injector, BankCard bankCard) {
        super(context, injector);
        this.bankCard = bankCard;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        String toolBarTitle = String.format("%s •••• %d",
                getContext().getResources().getString(obtainFinancialServiceType(bankCard.type())), bankCard.number() % 10000);
        Screen view = getView();

        view.setTitle(toolBarTitle);
        view.showCardBankInfo(bankCard);
        view.showDefaultAddress(obtainAddressWithCountry());
    }

    private AddressInfoWithLocale obtainAddressWithCountry() {
        //todo rewrite in commands
        User user = userSessionHolder.get().get().getUser();

        AddressInfo addressInfo = ImmutableAddressInfo.builder()
                .address1("12345 Lollipop Drive")
                .address2("Apt 123")
                .city("New York")
                .state("NY")
                .zip("10010")
                .build();

        return ImmutableAddressInfoWithLocale.builder()
                .addressInfo(addressInfo)
                .locale(localeHelper.getMappedLocale(user))
                .build();
    }

    @StringRes
    private int obtainFinancialServiceType(Record.FinancialService financialService) {
        switch (financialService) {
            case VISA:
                return R.string.wallet_card_financial_service_visa;
            case MASTERCARD:
                return R.string.wallet_card_financial_service_master_card;
            case DISCOVER:
                return R.string.wallet_card_financial_service_discover;
            case AMEX:
                return R.string.wallet_card_financial_service_amex;
            default: throw new IllegalStateException("Incorrect Financial Service");
        }
    }

    public void onDeleteCardRequired() {

    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public interface Screen extends WalletScreen {
        void setTitle(String title);

        void showCardBankInfo(BankCard bankCard);

        void showDefaultAddress(AddressInfoWithLocale addressInfoWithLocale);
    }

}
