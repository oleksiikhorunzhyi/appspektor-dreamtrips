package com.messenger.ui.module.flagging;


import android.support.annotation.StringRes;

import com.messenger.ui.module.ModuleStatefulView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import rx.Observable;

public interface FlaggingView extends ModuleStatefulView<FlaggingPresenter> {

   void showFlagsLoadingDialog();

   void hideFlagsLoadingDialog();

   void showFlagsListDialog(List<Flag> flags);

   Observable<CharSequence> showFlagReasonDialog(Flag flag, String prefilledReason);

   void showFlagConfirmationDialog(Flag flag);

   void showFlaggingProgressDialog();

   void hideFlaggingProgressDialog();

   void showFlaggingSuccess();

   void showFlaggingError();

   void showError(String message);

   void showError(@StringRes int message);

   Observable<Void> getCanceledDialogsStream();
}
