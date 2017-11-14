package com.worldventures.wallet.domain.storage.persistent;

import android.support.annotation.Nullable;

interface PersistentStorageNameProvider {

   @Nullable
   String folderName();
}
