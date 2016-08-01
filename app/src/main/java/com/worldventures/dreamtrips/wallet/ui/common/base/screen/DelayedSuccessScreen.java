package com.worldventures.dreamtrips.wallet.ui.common.base.screen;

public interface DelayedSuccessScreen extends ProgressErrorScreen {
    void showSuccessWithDelay(Runnable action, long delay);
}
