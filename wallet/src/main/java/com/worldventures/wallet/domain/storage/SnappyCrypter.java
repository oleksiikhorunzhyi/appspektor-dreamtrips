package com.worldventures.wallet.domain.storage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.wallet.domain.storage.security.crypto.Crypter;
import com.worldventures.wallet.domain.storage.security.crypto.HybridAndroidCrypter;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;

public class SnappyCrypter {

   private static final int INITIAL_BUFFER_SIZE = 4096;
   private static final int MAX_BUFFER_SIZE = -1;

   private final Kryo kryo;
   private final HybridAndroidCrypter crypter;

   SnappyCrypter(Kryo kryo, HybridAndroidCrypter crypter) {
      this.kryo = kryo;
      this.crypter = crypter;
   }

   public void put(DB db, String key, Object obj) throws SnappydbException {
      Output output = new Output(INITIAL_BUFFER_SIZE, MAX_BUFFER_SIZE);
      kryo.writeClassAndObject(output, obj);
      db.put(key, output.toBytes());
   }

   public void putEncrypted(DB db, String key, Object obj) throws SnappydbException {
      Output output = new Output(INITIAL_BUFFER_SIZE, MAX_BUFFER_SIZE);
      kryo.writeClassAndObject(output, obj);
      byte[] bytes = crypter.encrypt(new Crypter.CryptoData(new ByteArrayInputStream(output.getBuffer())))
            .toByteArray();
      db.put(key, bytes);
   }

   public <T> T get(DB db, String key, Class<T> clazz) throws SnappydbException {
      T result = null;
      Input input = new Input();
      try {
         input.setBuffer(db.getBytes(key));
         result = (T) kryo.readClassAndObject(input);
      } finally {
         input.close();
      }
      return result;
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

   public <T> List<T> getList(DB db, String key) throws SnappydbException {
      List data = get(db, key, List.class);
      if (data == null) {
         return Collections.emptyList();
      } else {
         return (List<T>) data;
      }
   }

   public <T> List<T> getEncryptedList(DB db, String key) throws SnappydbException {
      List decrypted = getEncrypted(db, key, List.class);
      if (decrypted == null) {
         return Collections.emptyList();
      } else {
         return (List<T>) decrypted;
      }
   }

}
