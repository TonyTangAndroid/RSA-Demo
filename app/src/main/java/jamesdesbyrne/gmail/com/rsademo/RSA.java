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
 */
public class RSA {

    // Variables used throughtout program
    private KeyPairGenerator kpgen;
    private KeyPair kp;
    private Cipher ciph1, ciph2;
    private byte [] encBytes,decBytes;
    private String enc, dec;

    private static class Keys{
        PrivateKey priKey;
        PublicKey pubKey;
    }

    public Keys genKeys() throws NoSuchAlgorithmException {
        // Tells KeyPairGenerator to use RSA
        // Initialises the pair to be a 1024 bit prime number
        // Generate the public and private Keys
        kpgen = KeyPairGenerator.getInstance("RSA");
        kpgen.initialize(1024);
        kp = kpgen.genKeyPair();

        Keys k = new Keys();
        k.pubKey = kp.getPublic();
        k.priKey = kp.getPrivate();

        return k;
    }

    public String Encrypt(final String plainText)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        // gets the existing keys
        Keys k = new Keys();

        // Uses RSA
        // Sets encryption mode and the constant to use
        // Encrypts the message using RSA and the public Key
        ciph1 = Cipher.getInstance("RSA");
        ciph1.init(Cipher.ENCRYPT_MODE, k.pubKey);
        encBytes = ciph1.doFinal(plainText.getBytes());
        enc = new String(encBytes);

        //Returns the encrypted text
        return enc;
    }

    // Decrypts the message using RSA
    // The keypair was generated above when the
    public String RSADecrypt (final String result)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        // gets the existing keys
        Keys k = new Keys();

        // Uses RSA
        // Sets decryption mode and the constant to use
        // Decrypts the message using RSA and the private Key
        ciph1=Cipher.getInstance("RSA");
        ciph1.init(Cipher.DECRYPT_MODE, k.priKey);
        decBytes = ciph1.doFinal(result.getBytes());
        dec = new String(decBytes);

        //returns the decrypted string
        return dec;
    }
}
