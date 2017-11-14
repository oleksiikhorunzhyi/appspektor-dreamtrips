package com.messenger.messengerservers.listeners;

import com.messenger.messengerservers.model.Message;

public interface GlobalMessageListener {

   void onReceiveMessage(Message message);

   void onPreSendMessage(Message message);

   void onSendMessage(Message message);

   void onErrorMessage(Message message);

   class SimpleGlobalMessageListener implements GlobalMessageListener {

      @Override
      public void onReceiveMessage(Message message) {
         //do nothing
      }

      @Override
      public void onPreSendMessage(Message message) {
         //do nothing
      }

      @Override
      public void onSendMessage(Message message) {
         //do nothing
      }

      @Override
      public void onErrorMessage(Message message) {
         //do nothing
      }
   }
}
