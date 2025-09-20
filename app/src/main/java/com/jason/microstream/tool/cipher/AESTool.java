package com.jason.microstream.tool.cipher;

import android.annotation.SuppressLint;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 *Symmetric-key algorithm,with Android KeyStore
 */
public class AESTool {

    private static KeyStore keyStore;
    private static final String TRANSFORMATION_MODE = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    //    private static final String ALIAS = "AndroidKeyStore";
    private static final String ENCODE = "UTF-8";
    private static final int ALIAS_SIZE = 32;
    private static int IV_SIZE = 17;

    public static void init() {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (KeyStoreException e) {

        } catch (CertificateException e) {

        } catch (NoSuchAlgorithmException e) {

        } catch (IOException e) {

        }
    }


    @SuppressLint("NewApi")
    public static String newEncrypt(String ss) {
        if (keyStore == null) init();

        Cipher cipher = null;
        String ALIAS = getRandomAlias();
        byte[] bytes = new byte[0];
        try {
            bytes = ss.getBytes(ENCODE);
        } catch (UnsupportedEncodingException e) {

        }

        try {
            cipher = Cipher.getInstance(TRANSFORMATION_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(ALIAS));
            byte[] iv = cipher.getIV();
            byte[] cryptBytes = cipher.doFinal(bytes);
            String cryptString = Base64.encodeToString(cryptBytes, Base64.DEFAULT);
            String ivString = Base64.encodeToString(iv, Base64.DEFAULT);
            return ALIAS + ivString + cryptString;
        } catch (InvalidKeyException e) {

        } catch (NoSuchAlgorithmException e) {

        } catch (NoSuchPaddingException e) {

        } catch (IllegalBlockSizeException e) {

        } catch (BadPaddingException e) {

        }
        return null;
    }

    public static String newDecrypt(String target) {
        try {
            if (keyStore == null) init();
            Cipher cipher = null;
            String ALIAS = target.substring(0, ALIAS_SIZE);
            String IV = target.substring(ALIAS_SIZE, ALIAS_SIZE + IV_SIZE);
            String data = target.substring(ALIAS_SIZE + IV_SIZE, target.length());
            byte[] bytes = Base64.decode(data, Base64.DEFAULT);

            cipher = Cipher.getInstance(TRANSFORMATION_MODE);
            SecretKey secretKey = getSecretKey(ALIAS);
            if (secretKey == null) return null;
            GCMParameterSpec spec = new GCMParameterSpec(128, Base64.decode(IV, Base64.DEFAULT));
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            byte[] text = cipher.doFinal(bytes);
            return new String(text, ENCODE);
        } catch (Throwable e) {

        }
        return "";
    }


    private static String getRandomAlias() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    private static SecretKey getSecretKey(final String alias) {
        try {
            if (keyStore.containsAlias(alias)) {
                return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
            } else {
                return generateKey(alias);
            }
        } catch (KeyStoreException e) {

        } catch (NoSuchAlgorithmException e) {

        } catch (UnrecoverableEntryException e) {

        }
        return null;
    }

    private static SecretKey generateKey(final String alias) {
        try {
            KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {

        } catch (NoSuchProviderException e) {

        } catch (InvalidAlgorithmParameterException e) {

        }
        return null;
    }

}
