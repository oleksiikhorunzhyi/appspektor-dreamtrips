package com.worldventures.dreamtrips.wallet.domain.storage.security.crypto;

import android.content.Context;
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

import timber.log.Timber;

public class DreamTripsCrypter implements Crypter<ByteArrayInputStream, ByteArrayOutputStream> {

   private static final String ASYMMETRIC_KEY_TYPE = "RSA/ECB/PKCS1Padding";
   private static final String ASYMMETRIC_KEY_PROVIDER = "AndroidOpenSSL";
   private static final String ASYMMETRIC_KEY_SUBJECT = "CN=DreamTrips, O=WorldVentures";
   private static final String SYMMETRIC_KEY_TYPE = "AES";
   private static final int SYMMETRIC_KEY_SIZE = 256;

   private final Context context;
   private final String keyAlias;

   private final KeyStore keyStore;
   private final Cipher asymmetricCipher;
   private final Cipher symmetricCipher;
   private SecretKeySpec symmetricKeySpec;
   private final File symmetricKeyFile;
   private static final String SYMMETRIC_KEY_FILENAME = "security_key";

   public DreamTripsCrypter(Context context, String alias) throws IllegalStateException {
      this.context = context.getApplicationContext();
      this.keyAlias = alias;

      try {
         keyStore = KeyStore.getInstance("AndroidKeyStore");
         keyStore.load(null);
         asymmetricCipher = Cipher.getInstance(ASYMMETRIC_KEY_TYPE);
         symmetricCipher = Cipher.getInstance(SYMMETRIC_KEY_TYPE);
         symmetricKeyFile = new File(context.getFilesDir(), SYMMETRIC_KEY_FILENAME);
      } catch (Exception e) {
         throw new IllegalStateException("Can't load keystore");
      }
   }

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
      try {
         initSymmetricKeyIfNeeded();
      } catch (Exception e) {
         throw new IllegalStateException("Can't init symmetric key", e);
      }
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
      try {
         initSymmetricKeyIfNeeded();
      } catch (Exception e) {
         throw new IllegalStateException("Can't init symmetric key", e);
      }
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

   public void deleteKey(String alias) {
      try {
         keyStore.deleteEntry(alias);
      } catch (KeyStoreException e) {
         Timber.e(e, "Failed to delete key");
      }
   }

   private synchronized void prepareKeystore() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      if (keyStore.containsAlias(keyAlias)) return;
      //
      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      end.add(Calendar.YEAR, 5);
      KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
            .setAlias(keyAlias)
            .setKeySize(1024)
            .setSubject(new X500Principal(ASYMMETRIC_KEY_SUBJECT))
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.getTime())
            .setEndDate(end.getTime())
            .build();
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");

      generator.initialize(spec);
      generator.generateKeyPair();
   }

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
      prepareKeystore();
      KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
      PrivateKey privateKey = privateKeyEntry.getPrivateKey();
      // read Symmetric key
      asymmetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] symmetricKey = new byte[SYMMETRIC_KEY_SIZE / 8];
      CipherInputStream is = new CipherInputStream(new FileInputStream(symmetricKeyFile), asymmetricCipher);
      is.read(symmetricKey);
      return new SecretKeySpec(symmetricKey, SYMMETRIC_KEY_TYPE);
   }

   private SecretKeySpec createAndSaveSymmetricKey() throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
      // load Asymmetric key
      prepareKeystore();
      KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias, null);
      RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();
      // create Symmetric key
      KeyGenerator kgen = KeyGenerator.getInstance(SYMMETRIC_KEY_TYPE);
      kgen.init(SYMMETRIC_KEY_SIZE);
      SecretKey aesKey = kgen.generateKey();
      // write Symmetric key
      asymmetricCipher.init(Cipher.ENCRYPT_MODE, publicKey);
      CipherOutputStream os = new CipherOutputStream(new FileOutputStream(symmetricKeyFile), asymmetricCipher);
      os.write(aesKey.getEncoded());
      os.close();

      return new SecretKeySpec(aesKey.getEncoded(), SYMMETRIC_KEY_TYPE);
   }

}