package jamesdesbyrne.gmail.com.rsademo;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import hugo.weaving.DebugLog;

/**
 * Created by james on 25/02/15.
 * - Encrypts messages using RSAUtil
 * - Decrypts messages using RSAUtil
 * - Generates public and private keys
 */
@DebugLog
public class RSAUtil {

    public static final String PRIVATE_PEM = "private.pem";
    public static final String PUBLIC_PEM = "public.pem";
    private final Context context;
    // Holds the cipher text
    private Cipher cipher;

    private static PrivateKey priKey;
    private static PublicKey pubKey;

    // Variables for taking input and
    // returning a useful output
    byte[] encBytes, decBytes;
    String enc, dec;
    int size = 1024;

    public RSAUtil(Context context) {
        this.context = context;
    }

    public void setSize(int _size) {
        size = _size;
    }

    public String getPriKey() {
        return priKey.getFormat();
    }

    public String getPubKey() {
        return pubKey.toString();
    }

    public void genKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator kpgen;
        KeyPair kp;
        kpgen = KeyPairGenerator.getInstance("RSA");
        kpgen.initialize(size);
        kp = kpgen.genKeyPair();

        pubKey = kp.getPublic();
        priKey = kp.getPrivate();
        saveKey();

    }


    private void saveKey() {
        {
            byte[] encoded = pubKey.getEncoded();
            String base64Encoded = Base64.encodeToString(encoded, Base64.NO_WRAP);
            writeToFile(base64Encoded, context, PUBLIC_PEM);
        }
        {
            byte[] encoded = priKey.getEncoded();
            String base64Encoded = Base64.encodeToString(encoded, Base64.NO_WRAP);
            writeToFile(base64Encoded, context, PRIVATE_PEM);
        }
    }

    private String readFromFile(Context context, String name) {

        String ret = "";

        try {
            InputStream inputStream = new FileInputStream(new File(getCacheDir(context), name));

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


    private void writeToFile(String data, Context context, String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(getCacheDir(context), name)));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private File getCacheDir(Context context) {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    }


    public String encrypt(final String plainText)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        //Checks that a key exists
        if (pubKey == null || priKey == null) loadKey();

        // Uses RSAUtil
        // Sets encryption mode and the constant to use
        // Encrypts the message using RSAUtil and the public Key
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        encBytes = cipher.doFinal(plainText.getBytes());
        enc = new String(encBytes);

        //Returns the encrypted text
        return enc;
    }

    private void loadKey() throws NoSuchAlgorithmException {

        if (needToGenerateKey()) {
            genKeys();
        } else {
            initKeys();
        }

    }

    @DebugLog
    private boolean needToGenerateKey() {
        File file = new File(getCacheDir(context), PUBLIC_PEM);
        return !file.exists();
    }

    private void initKeys() {

        try {

            {
                byte[] keyBytes = Base64.decode(publicKeyString(), Base64.NO_WRAP);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                pubKey = keyFactory.generatePublic(spec);

            }


            {
                byte[] keyBytes = Base64.decode(privateKeyString(), Base64.NO_WRAP);
                PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                priKey = keyFactory.generatePrivate(spec);

            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private String privateKeyString() {
        return readFromFile(context, PRIVATE_PEM);
    }

    private String publicKeyString() {
        return readFromFile(context, PUBLIC_PEM);
    }

    // Decrypts the message using RSAUtil
    public String decrypt()
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        //Checks that a key exists
        if (pubKey == null || priKey == null) loadKey();

        // Uses RSAUtil
        // Sets decryption mode and the constant to use
        // Decrypts the message using RSAUtil and the private Key
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        decBytes = cipher.doFinal(encBytes);
        dec = new String(decBytes);

        //returns the decrypted string
        return dec;
    }
}
