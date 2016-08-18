package com.worldventures.dreamtrips.wallet.ui.wizard.success;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.flow.path.StyledPath;

public interface ScreenContent {

   String title();

   String buttonText();

   String text();

   @NonNull StyledPath nextPath();
}
