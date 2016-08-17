package com.messenger.ui.module.flagging;

import android.os.Parcel;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.ArrayList;
import java.util.List;

public class FlaggingState implements Parcelable {

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

   ///////////////////////////////////////////////////////////////////////////
   // Parcelable
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeList(this.flags);
      dest.writeString(this.messageId);
      dest.writeString(this.conversationId);
      dest.writeParcelable(this.flag, 0);
      dest.writeString(this.reasonDescription);
      dest.writeInt(this.dialogState == null ? -1 : this.dialogState.ordinal());
   }

   protected FlaggingState(Parcel in) {
      this.flags = new ArrayList<Flag>();
      in.readList(this.flags, Flag.class.getClassLoader());
      this.messageId = in.readString();
      this.conversationId = in.readString();
      this.flag = in.readParcelable(Flag.class.getClassLoader());
      this.reasonDescription = in.readString();
      int tmpDialogState = in.readInt();
      this.dialogState = tmpDialogState == -1 ? null : DialogState.values()[tmpDialogState];
   }

   public static final Creator<FlaggingState> CREATOR = new Creator<FlaggingState>() {
      @Override
      public FlaggingState createFromParcel(Parcel source) {
         return new FlaggingState(source);
      }

      @Override
      public FlaggingState[] newArray(int size) {
         return new FlaggingState[size];
      }
   };
}
