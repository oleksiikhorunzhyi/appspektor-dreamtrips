package com.worldventures.dreamtrips.wallet.domain.storage.persistent;

import android.support.annotation.Nullable;

interface PersistentStorageNameProvider {

   @Nullable
   String folderName();
}
