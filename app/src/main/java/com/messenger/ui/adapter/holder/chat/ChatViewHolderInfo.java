package com.messenger.ui.adapter.holder.chat;

public class ChatViewHolderInfo {

    private int viewType;
    private Class<? extends MessageViewHolder> viewHolderClass;
    private boolean systemMessage;
    private boolean own;
    private String attachmentType;

    public int getViewType() {
        return viewType;
    }

    public Class<? extends MessageViewHolder> getViewHolderClass() {
        return viewHolderClass;
    }

    public boolean isOwn() {
        return own;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public boolean isSystemMessage() {
        return systemMessage;
    }

    public static class Builder {
        ChatViewHolderInfo info;

        public Builder() {
            info = new ChatViewHolderInfo();
        }

        public Builder viewType(int viewType) {
            info.viewType = viewType;
            return this;
        }

        public Builder viewHolderClass(Class<? extends MessageViewHolder> viewHolderClass) {
            info.viewHolderClass = viewHolderClass;
            return this;
        }

        public Builder systemMessage(boolean systemMessage) {
            info.systemMessage = systemMessage;
            return this;
        }

        public Builder own(boolean own) {
            info.own = own;
            return this;
        }

        public Builder attachmentType(String attachmentType) {
            info.attachmentType = attachmentType;
            return this;
        }

        public ChatViewHolderInfo build() {
            return info;
        }
    }
}
