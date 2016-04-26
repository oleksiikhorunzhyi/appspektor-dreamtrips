package com.messenger.ui.adapter.holder.chat;

public class ChatViewHolderInfo {

    private int viewType;
    private Class<? extends MessageViewHolder> viewHolderClass;
    private boolean own;
    private String type;

    public ChatViewHolderInfo(int viewType, Class<? extends MessageViewHolder> viewHolderClass, boolean own) {
        this.viewType = viewType;
        this.viewHolderClass = viewHolderClass;
        this.own = own;
    }

    public ChatViewHolderInfo(int viewType, Class<? extends MessageViewHolder> viewHolderClass, boolean own, String type) {
        this(viewType, viewHolderClass, own);
        this.type = type;
    }

    public int getViewType() {
        return viewType;
    }

    public Class<? extends MessageViewHolder> getViewHolderClass() {
        return viewHolderClass;
    }

    public boolean isOwn() {
        return own;
    }

    public String getType() {
        return type;
    }
}
