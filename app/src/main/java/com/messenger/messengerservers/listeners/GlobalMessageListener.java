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
      }

      @Override
      public void onPreSendMessage(Message message) {

      }

      @Override
      public void onSendMessage(Message message) {
      }

      @Override
      public void onErrorMessage(Message message) {
      }
   }
}
