package jamesdesbyrne.gmail.com.rsademo;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by james on 25/02/15.
 * - Encrypts messages using RSA
 * - Decrypts messages using RSA
 * - Generates public and private keys
 */
public class RSA {

    // Holds the cipher text
    private Cipher cipher;

    private static PrivateKey priKey;
    private static PublicKey pubKey;

    public String getPriKey(){
        return priKey.toString();
    }
    public String getPubKey(){
        return pubKey.toString();
    }

    public void genKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator kpgen;
        KeyPair kp;
        // Tells KeyPairGenerator to use RSA
        // Initialises the pair to be a 4 bit prime number
        // --> This is deliberately a small number to accommodate screen size
        // Generate the public and private Keys
        kpgen = KeyPairGenerator.getInstance("RSA");
        kpgen.initialize(128);
        kp = kpgen.genKeyPair();

        pubKey = kp.getPublic();
        priKey = kp.getPrivate();
    }

    public String encrypt(final String plainText)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        // Variables for taking input and
        // returning a useful output
        byte [] encBytes;
        String enc;

        //Checks that a key exists
        if(pubKey == null || priKey == null) genKeys();

        // Uses RSA
        // Sets encryption mode and the constant to use
        // Encrypts the message using RSA and the public Key
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        encBytes = cipher.doFinal(plainText.getBytes());
        enc = new String(encBytes);

        //Returns the encrypted text
        return enc;
    }

    // Decrypts the message using RSA
    // The keypair was generated above when the
    public String decrypt (final String result)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        // Variables for taking input and
        // returning a useful output
        byte [] decBytes;
        String dec;

        //Checks that a key exists
        if(pubKey == null || priKey == null) genKeys();

        // Uses RSA
        // Sets decryption mode and the constant to use
        // Decrypts the message using RSA and the private Key
        cipher=Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        decBytes = cipher.doFinal(result.getBytes());
        dec = new String(decBytes);

        //returns the decrypted string
        return dec;
    }
}
