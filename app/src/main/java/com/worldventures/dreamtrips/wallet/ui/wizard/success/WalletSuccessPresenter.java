package com.worldventures.dreamtrips.wallet.ui.wizard.success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import flow.Flow;

public class WalletSuccessPresenter extends WalletPresenter<WalletScreen, Parcelable> {

    private final StyledPath styledPath;

    public WalletSuccessPresenter(Context context, Injector injector, StyledPath styledPath) {
        super(context, injector);
        this.styledPath = styledPath;
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public void goToNext() {
        Flow.get(getContext()).set(styledPath);
    }
}
