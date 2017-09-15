package com.messenger.ui.module.flagging;


import com.messenger.ui.module.ModuleStatefulPresenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Flag;

public interface FlaggingPresenter extends ModuleStatefulPresenter<FlaggingView, FlaggingState> {

   void flagMessage(String conversationId, String messageId);

   void onFlagTypeChosen(Flag flag);

   void onFlagReasonProvided(String reason);

   void onFlagMessageConfirmation();
}
