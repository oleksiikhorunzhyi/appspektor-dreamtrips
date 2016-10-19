package com.worldventures.dreamtrips.wallet.domain.storage.security.crypto;

import java.io.InputStream;
import java.io.OutputStream;

public interface Crypter<IS extends InputStream, OS extends OutputStream> {

   OS encrypt(CryptoData<IS> cryptoData);

   OS decrypt(CryptoData<IS> decryptBundle);

   class CryptoData<T extends InputStream> {
      public final T is;

      public CryptoData(T is) {
         this.is = is;
      }

   }

}