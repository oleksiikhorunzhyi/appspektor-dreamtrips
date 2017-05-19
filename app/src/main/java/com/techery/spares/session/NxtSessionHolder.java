package com.techery.spares.session;

import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableNxtSession;
import com.worldventures.dreamtrips.wallet.service.nxt.model.NxtSession;

public class NxtSessionHolder extends ComplexObjectStorage<NxtSession> {

   private static final String NXT_SESSION_KEY = "NXT_SESSION_KEY";

   public NxtSessionHolder(SimpleKeyValueStorage keyValueStorage) {
      super(keyValueStorage, NXT_SESSION_KEY, ImmutableNxtSession.class);
   }
}
