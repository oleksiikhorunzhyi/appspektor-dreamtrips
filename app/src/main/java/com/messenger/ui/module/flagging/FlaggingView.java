package com.messenger.ui.module.flagging;


import com.messenger.ui.module.ModuleView;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

public interface FlaggingView extends ModuleView<FlaggingPresenter> {

    void showFlagsLoadingDialog();

    void hideFlagsLoadingDialog();

    void showFlagsListDialog(List<Flag> flags);

    void showFlagReasonDialog(Flag flag);

    void showFlagConfirmationDialog(Flag flag);

    void showFlaggingProgressDialog();

    void hideFlaggingProgressDialog();

    void showFlaggingSuccess();

    void showFlaggingError();

    void showError(String message);
}
