package com.messenger.ui.module.flagging;

import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;
import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.messenger.delegate.chat.flagging.FlagMessageCommand;
import com.messenger.delegate.chat.flagging.FlagMessageDelegate;
import com.messenger.delegate.chat.flagging.ImmutableFlagMessageDTO;
import com.messenger.ui.module.ModuleStatefulPresenterImpl;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FlaggingPresenterImpl extends ModuleStatefulPresenterImpl<FlaggingView, FlaggingState> implements FlaggingPresenter {

   @Inject FlagsInteractor flagsInteractor;
   @Inject FlagMessageDelegate flagMessageDelegate;

   private Subscription getFlagsSubscription;

   public FlaggingPresenterImpl(FlaggingView view, Injector injector) {
      super(view);
      injector.inject(this);
      bindToFlagging();
      view.getCanceledDialogsStream().subscribe(aVoid -> resetState());
   }

   @Override
   protected FlaggingState createNewState() {
      return new FlaggingState();
   }

   @Override
   public void flagMessage(String conversationId, String messageId) {
      resetState();
      getState().setMessageId(messageId);
      getState().setConversationId(conversationId);
      getState().setDialogState(FlaggingState.DialogState.LOADING_FLAGS);
      loadFlags();
   }

   private void bindToFlagging() {
      flagMessageDelegate.observeOngoingFlagging()
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<FlagMessageCommand>().onStart(this::onFlaggingStarted)
                  .onSuccess(this::onFlaggingSuccess)
                  .onFail(this::onFlagginError));
   }

   private void onFlaggingSuccess(FlagMessageCommand action) {
      Timber.d("[Flagging] Result obtained, stop progress");
      flagMessageDelegate.clearReplays();
      getView().hideFlaggingProgressDialog();
      if (TextUtils.equals(action.getResult().messageId(), getState().getMessageId())) {
         getView().showFlaggingSuccess();
      }
      //
      resetState();
   }

   private void onFlagginError(FlagMessageCommand action, Throwable e) {
      Timber.e(e, "Smth went wrong while flagging");
      flagMessageDelegate.clearReplays();
      getView().hideFlaggingProgressDialog();
      getView().showFlaggingError();
      //
      resetState();
   }

   private void onFlaggingStarted(FlagMessageCommand action) {
      Timber.d("[Flagging] Flagging is in progress, wait");
      getView().showFlaggingProgressDialog();
   }

   private void loadFlags() {
      getView().showFlagsLoadingDialog();
      if (getFlagsSubscription != null) getFlagsSubscription.unsubscribe();
      getFlagsSubscription = flagsInteractor.getFlagsPipe()
            .createObservable(new GetFlagsCommand())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetFlagsCommand>()
               .onSuccess(this::onFlagsLoadingSuccess)
               .onFail(this::onFlagsLoadingError));
   }

   private void onFlagsLoadingSuccess(GetFlagsCommand getFlagsCommand) {
      getView().hideFlagsLoadingDialog();
      getState().setDialogState(FlaggingState.DialogState.FLAGS_LIST);
      getState().setFlags(getFlagsCommand.getResult());
      showFlagsListDialog();
   }

   private void onFlagsLoadingError(GetFlagsCommand command, Throwable e) {
      getView().hideFlagsLoadingDialog();
      getView().showError(command.getErrorMessage());
      Timber.e(e, "[Flagging] Could not load flags");
   }

   private void showFlagsListDialog() {
      getView().showFlagsListDialog(getState().getFlags());
   }

   @Override
   public void onFlagTypeChosen(Flag flag) {
      getState().setFlag(flag);
      if (flag.isRequireDescription()) {
         getState().setDialogState(FlaggingState.DialogState.REASON);
         showFlagReasonDialog();
      } else {
         getState().setDialogState(FlaggingState.DialogState.CONFIRMATION);
         showFlagConfirmationDialog();
      }
   }

   private void showFlagReasonDialog() {
      getView().showFlagReasonDialog(getState().getFlag(), getState().getReasonDescription())
            .subscribe(text -> getState().setReasonDescription(text.toString()));
   }

   @Override
   public void onFlagReasonProvided(String reason) {
      getState().setReasonDescription(reason);
      getState().setDialogState(FlaggingState.DialogState.CONFIRMATION);
      showFlagConfirmationDialog();
   }

   private void showFlagConfirmationDialog() {
      getView().showFlagConfirmationDialog(getState().getFlag());
   }

   @Override
   public void onFlagMessageConfirmation() {
      sendFlag();
   }

   private void sendFlag() {
      FlaggingState flaggingState = getState();
      flaggingState.setDialogState(FlaggingState.DialogState.PROGRESS);
      Timber.d("Sending flag %s with custom reason %s for message %s", flaggingState.getFlag()
            .getName(), flaggingState.getReasonDescription(), flaggingState.getMessageId());
      //
      flagMessageDelegate.flagMessage(ImmutableFlagMessageDTO.builder()
            .messageId(flaggingState.getMessageId())
            .groupId(flaggingState.getConversationId())
            .reasonId(String.valueOf(flaggingState.getFlag().getId()))
            .reasonDescription(flaggingState.getReasonDescription())
            .build());
   }

   @Override
   public void applyState(FlaggingState state) {
      setState(state);
      switch (state.getDialogState()) {
         case LOADING_FLAGS:
            loadFlags();
            break;
         case FLAGS_LIST:
            showFlagsListDialog();
            break;
         case REASON:
            showFlagReasonDialog();
            break;
         case CONFIRMATION:
            showFlagConfirmationDialog();
            break;
         case PROGRESS:
            getView().showFlaggingProgressDialog();
            break;
      }
   }
}
