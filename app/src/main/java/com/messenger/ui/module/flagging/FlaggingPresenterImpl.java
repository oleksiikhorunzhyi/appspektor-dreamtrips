package com.messenger.ui.module.flagging;

import android.os.Handler;

import com.messenger.delegate.FlagsDelegate;
import com.messenger.entities.DataMessage;
import com.messenger.ui.module.ModuleStatefulPresenterImpl;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FlaggingPresenterImpl extends ModuleStatefulPresenterImpl<FlaggingView, FlaggingState>
        implements FlaggingPresenter {

    @Inject FlagsDelegate flagsDelegate;

    private Subscription getFlagsSubscription;

    public FlaggingPresenterImpl(FlaggingView view, Injector injector) {
        super(view);
        injector.inject(this);
        view.setPresenter(this);
    }

    @Override
    protected FlaggingState createNewState() {
        return new FlaggingState();
    }

    @Override
    public void flagMessage(DataMessage message) {
        getState().setMessage(message);
        getState().setDialogState(FlaggingState.DialogState.LOADING_FLAGS);
        loadFlags();
    }

    private void loadFlags() {
        getView().showFlagsLoadingDialog();
        if (getFlagsSubscription != null) getFlagsSubscription.unsubscribe();
        getFlagsSubscription = flagsDelegate.getFlags()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(flagsAction -> {
                    getView().hideFlagsLoadingDialog();
                    getState().setDialogState(FlaggingState.DialogState.FLAGS_LIST);
                    getState().setFlags(flagsAction.getFlags());
                    showFlagsListDialog();
                }, e -> Timber.e(e, "Could not get flags"));
    }

    private void showFlagsListDialog() {
        getView().showFlagsListDialog(getState().getFlags());
    }

    @Override
    public void onFlagTypeChosen(Flag flag) {
        getState().setFlag(flag);
        if (flag.isRequireDescription()) {
            showFlagReasonDialog();
        } else {
            showFlagConfirmationDialog();
        }
    }

    private void showFlagReasonDialog() {
        getView().showFlagReasonDialog(getState().getFlag());
    }

    @Override
    public void onFlagReasonProvided(String reason) {
        getState().setReason(reason);
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
        //TODO Temp code until flagging is implemented
        Timber.d("Sending flag %s with custom reason %s for message %s",
                getState().getFlag().getName(), getState().getReason(), getState().getMessage());
        getState().setDialogState(FlaggingState.DialogState.PROGRESS);
        showFlaggingProgressDialog();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            reportFlaggingCompleted();
        }, 2000);
    }

    private void showFlaggingProgressDialog() {
        getView().showFlaggingProgressDialog();
    }

    private void reportFlaggingCompleted() {
        if (getView() != null) getView().hideFlaggingProgressDialog();
        setState(createNewState());
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
                showFlaggingProgressDialog();
                break;
        }
    }
}
