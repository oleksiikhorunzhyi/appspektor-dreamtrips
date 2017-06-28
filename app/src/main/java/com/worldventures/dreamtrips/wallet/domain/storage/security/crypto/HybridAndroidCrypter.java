package com.worldventures.dreamtrips.wallet.domain.storage.security.crypto;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * Encrypt/Decrypt utility for Android.
 * Hybrid encryption is applied: data is x-crypted symmetrically and symmetric key is x-crypted asymmetrically.
 * Asymmetric key is taken from Android KeyStore by alias. Symmetric key is stored locally in app's file cache.
 */
public class HybridAndroidCrypter implements Crypter<ByteArrayInputStream, ByteArrayOutputStream> {

   private final Context context;
   private final String keyAlias;

   private final AsymmetricKeyParams asymmetricParams;
   private final SymmetricKeyParams symmetricParams;

   private final KeyStore keyStore;
   private final Cipher asymmetricCipher;
   private final Cipher symmetricCipher;
   private volatile SecretKeySpec symmetricKeySpec;
   private final File symmetricKeyFile;

   public HybridAndroidCrypter(Context context, String alias, AsymmetricKeyParams asymmetricParams, SymmetricKeyParams symmetricParams) throws IllegalStateException {
      this(context, alias, alias + "-key", asymmetricParams, symmetricParams);
   }

   public HybridAndroidCrypter(Context context, String alias, String keyFilename, AsymmetricKeyParams asymmetricParams, SymmetricKeyParams symmetricParams) throws IllegalStateException {
      this.context = context.getApplicationContext();
      this.keyAlias = alias;
      this.asymmetricParams = asymmetricParams;
      this.symmetricParams = symmetricParams;

      try {
         keyStore = KeyStore.getInstance("AndroidKeyStore");
         keyStore.load(null);
         asymmetricCipher = Cipher.getInstance(this.asymmetricParams.keyType);
         symmetricCipher = Cipher.getInstance(this.symmetricParams.keyType);
         symmetricKeyFile = new File(context.getFilesDir(), keyFilename);
      } catch (Exception e) {
         throw new IllegalStateException("Can't load keystore");
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Data API
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public ByteArrayOutputStream encrypt(CryptoData<ByteArrayInputStream> cryptoData) {
      return encrypt(cryptoData.is);
   }

   @Override
   public ByteArrayOutputStream decrypt(CryptoData<ByteArrayInputStream> cryptoData) {
      return decrypt(cryptoData.is);
   }

   private ByteArrayOutputStream encrypt(ByteArrayInputStream is) throws IllegalStateException {
      if (is.available() == 0) return new ByteArrayOutputStream(0);
      initSymmetricKeyIfNeeded();
      try {
         symmetricCipher.init(Cipher.ENCRYPT_MODE, symmetricKeySpec);

         ByteArrayOutputStream os = new ByteArrayOutputStream();
         CipherOutputStream cipherOS = new CipherOutputStream(os, symmetricCipher);
         copy(is, cipherOS);
         cipherOS.close();
         return os;
      } catch (Exception e) {
         throw new IllegalStateException("Can't encrypt", e);
      }
   }

   private ByteArrayOutputStream decrypt(ByteArrayInputStream is) throws IllegalStateException {
      if (is.available() == 0) return new ByteArrayOutputStream(0);
      initSymmetricKeyIfNeeded();
      try {
         symmetricCipher.init(Cipher.DECRYPT_MODE, symmetricKeySpec);

         CipherInputStream cipherIS = new CipherInputStream(is, symmetricCipher);
         ByteArrayOutputStream os = new ByteArrayOutputStream();
         copy(cipherIS, os);
         cipherIS.close();
         return os;
      } catch (Exception e) {
         throw new IllegalStateException("Can't decrypt", e);
      }
   }

   private void copy(InputStream is, OutputStream os) throws IOException {
      int i;
      byte[] b = new byte[1024];
      while ((i = is.read(b)) != -1) {
         os.write(b, 0, i);
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Key manipulations
   ///////////////////////////////////////////////////////////////////////////

   private synchronized void initSymmetricKeyIfNeeded() throws IllegalStateException {
      if (symmetricKeySpec != null) return;
      //
      try {
         symmetricKeySpec = loadSymmetricKey();
         if (symmetricKeySpec == null) {
            symmetricKeySpec = createAndSaveSymmetricKey();
         }
      } catch (Exception e) {
         throw new IllegalStateException("Can't init Symmetric Key", e);
      }
   }

   private SecretKeySpec loadSymmetricKey() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, KeyStoreException, UnrecoverableEntryException, InvalidKeyException, IOException {
      if (!symmetricKeyFile.exists()) return null;
      // load Asymmetric key
      prepareAsymmetricKey();
      KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
      PrivateKey privateKey = privateKeyEntry.getPrivateKey();
      // read Symmetric key
      asymmetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] symmetricKey = new byte[symmetricParams.size / 8];
      CipherInputStream is = new CipherInputStream(new FileInputStream(symmetricKeyFile), asymmetricCipher);
      is.read(symmetricKey);
      return new SecretKeySpec(symmetricKey, symmetricParams.keyType);
   }

   private SecretKeySpec createAndSaveSymmetricKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
      // load Asymmetric key
      prepareAsymmetricKey();
      KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
      RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
      // create Symmetric key
      KeyGenerator kgen = KeyGenerator.getInstance(symmetricParams.keyType);
      kgen.init(symmetricParams.size);
      SecretKey aesKey = kgen.generateKey();
      // write Symmetric key
      asymmetricCipher.init(Cipher.ENCRYPT_MODE, publicKey);
      CipherOutputStream os = new CipherOutputStream(new FileOutputStream(symmetricKeyFile), asymmetricCipher);
      os.write(aesKey.getEncoded());
      os.close();

      return new SecretKeySpec(aesKey.getEncoded(), symmetricParams.keyType);
   }

   private void prepareAsymmetricKey() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      if (keyStore.containsAlias(keyAlias)) return;
      //
      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      end.add(Calendar.YEAR, 5);
      KeyPairGeneratorSpec.Builder builder = new KeyPairGeneratorSpec.Builder(context)
            .setAlias(keyAlias)
            .setSubject(new X500Principal(asymmetricParams.subject))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.getTime())
            .setEndDate(end.getTime());
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
         builder.setKeySize(asymmetricParams.size);
      }

      KeyPairGenerator generator = KeyPairGenerator.getInstance(asymmetricParams.algorithm, "AndroidKeyStore");

      generator.initialize(builder.build());
      generator.generateKeyPair();
   }

   public void deleteKeys() throws KeyStoreException {
      keyStore.deleteEntry(keyAlias);
      if (symmetricKeyFile.exists()) symmetricKeyFile.delete();
   }

   public static class AsymmetricKeyParams {
      public final String keyType;
      public final String algorithm;
      public final String subject;
      private final int size;

      public AsymmetricKeyParams(String keyType, String algorithm, String subject, int size) {
         this.keyType = keyType;
         this.algorithm = algorithm;
         this.subject = subject;
         this.size = size;
      }
   }

   public static class SymmetricKeyParams {
      public final String keyType;
      public final int size;

      public SymmetricKeyParams(String keyType, int size) {
         this.keyType = keyType;
         this.size = size;
      }
   }

}