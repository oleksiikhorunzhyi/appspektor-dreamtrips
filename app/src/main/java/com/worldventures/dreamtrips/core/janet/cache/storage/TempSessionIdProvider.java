package com.worldventures.dreamtrips.core.janet.cache.storage;

import java.io.IOException;

public interface TempSessionIdProvider {

   String getTempSessionId() throws IOException;
}
