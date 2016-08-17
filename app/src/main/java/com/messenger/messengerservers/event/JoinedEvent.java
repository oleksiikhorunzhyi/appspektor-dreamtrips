package com.messenger.messengerservers.event;

import com.messenger.messengerservers.model.Participant;

public class JoinedEvent {
   private Participant participant;
   private boolean isOnline;

   public JoinedEvent(Participant participant, boolean isOnline) {
      this.participant = participant;
      this.isOnline = isOnline;
   }

   public Participant getParticipant() {
      return participant;
   }

   public boolean isOnline() {
      return isOnline;
   }
}