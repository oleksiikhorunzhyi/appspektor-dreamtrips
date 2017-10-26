package com.worldventures.wallet.domain.session;

import com.worldventures.core.storage.complex_objects.ComplexObjectStorage;
import com.worldventures.core.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.wallet.service.nxt.model.ImmutableNxtSession;
import com.worldventures.wallet.service.nxt.model.NxtSession;

public class NxtSessionHolder extends ComplexObjectStorage<NxtSession> {

   private static final String NXT_SESSION_KEY = "NXT_SESSION_KEY";

   public NxtSessionHolder(SimpleKeyValueStorage keyValueStorage) {
      super(keyValueStorage, NXT_SESSION_KEY, ImmutableNxtSession.class);
   }
}
