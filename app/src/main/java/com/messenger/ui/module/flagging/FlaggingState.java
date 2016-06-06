package com.messenger.ui.module.flagging;

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

    private List<Flag> flags;
    private String messageId;
    private String conversationId;
    private Flag flag;
    private String reasonDescription;
    private DialogState dialogState;

    public FlaggingState() {
        dialogState = DialogState.NONE;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    ///////////////////////////////////////////////////////////////////////////

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
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

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public void setDialogState(DialogState dialogState) {
        this.dialogState = dialogState;
    }
}
