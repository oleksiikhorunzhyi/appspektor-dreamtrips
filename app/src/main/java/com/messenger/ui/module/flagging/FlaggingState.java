package com.messenger.ui.module.flagging;

import com.messenger.entities.DataMessage;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

public class FlaggingState {

    public enum DialogState {
        NONE,
        LOADING_FLAGS,
        FLAGS_LIST,
        REASON,
        CONFIRMATION,
        PROGRESS
    }

    private DataMessage message;
    private List<Flag> flags;
    private Flag flag;
    private String reason;
    private DialogState dialogState;

    public FlaggingState() {
        dialogState = DialogState.NONE;
    }

    public FlaggingState(DialogState dialogState) {
        this.dialogState = dialogState;
    }

    public void refresh(DialogState dialogState, DataMessage message, Flag flag) {
        refresh(dialogState, message, flag, null);
    }

    public void refresh(DialogState dialogState, DataMessage message, Flag flag, String reason) {
        this.message = message;
        this.dialogState = dialogState;
        this.flag = flag;
        this.reason = reason;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    ///////////////////////////////////////////////////////////////////////////

    public DataMessage getMessage() {
        return message;
    }

    public void setMessage(DataMessage message) {
        this.message = message;
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public void setDialogState(DialogState dialogState) {
        this.dialogState = dialogState;
    }
}
