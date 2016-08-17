package com.messenger.messengerservers.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;

import javax.security.auth.callback.CallbackHandler;

/**
 * The SASL WV mechanism.
 */
class SASLWVMechanism extends SASLMechanism {

   public static final String NAME = "WV";

   @Override
   protected void authenticateInternal(CallbackHandler cbh) throws SmackException {
      throw new UnsupportedOperationException("CallbackHandler not (yet) supported");
   }

   @Override
   protected byte[] getAuthenticationText() throws SmackException {
      return toBytes('\u0000' + authenticationId + '\u0000' + password);
   }

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public int getPriority() {
      return 10;
   }

   @Override
   public SASLWVMechanism newInstance() {
      return new SASLWVMechanism();
   }

   @Override
   public void checkIfSuccessfulOrThrow() throws SmackException {
      // No check performed
   }
}
