package com.worldventures.dreamtrips.core.repository;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.Crypter;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

public class SnappyCrypter {

   private final Kryo kryo;
   private final HybridAndroidCrypter crypter;

   public SnappyCrypter(Kryo kryo, HybridAndroidCrypter crypter) {
      this.kryo = kryo;
      this.crypter = crypter;
   }

   public void putEncrypted(DB db, String key, Object obj) throws SnappydbException {
      Output output = new Output(new ByteArrayOutputStream());
      kryo.writeClassAndObject(output, obj);
      byte[] bytes = crypter.encrypt(new Crypter.CryptoData(new ByteArrayInputStream(output.getBuffer())))
            .toByteArray();
      db.put(key, bytes);
   }

   public <T> T getEncrypted(DB db, String key, Class<T> clazz) throws SnappydbException {
      T result = null;
      Input input = new Input();
      try {
         byte[] bytes = crypter.decrypt(new Crypter.CryptoData(new ByteArrayInputStream(db.getBytes(key))))
               .toByteArray();
         input.setBuffer(bytes);
         result = (T) kryo.readClassAndObject(input);
      } finally {
         input.close();
      }
      return result;
   }

   public  <T> List<T> getEncryptedList(DB db, String key) throws SnappydbException {
      List decrypted = getEncrypted(db, key, List.class);
      if (decrypted == null) return Collections.emptyList();
      else return (List<T>) decrypted;
   }
}
