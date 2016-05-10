package com.messenger.ui.module.flagging;


import com.messenger.entities.DataMessage;
import com.messenger.ui.module.ModulePresenter;
import com.messenger.ui.module.ModuleStatefulPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

public interface FlaggingPresenter extends ModuleStatefulPresenter<FlaggingView, FlaggingState> {

    void flagMessage(DataMessage message);

    void onFlagTypeChosen(Flag flag);

    void onFlagReasonProvided(String reason);

    void onFlagMessageConfirmation();
}
