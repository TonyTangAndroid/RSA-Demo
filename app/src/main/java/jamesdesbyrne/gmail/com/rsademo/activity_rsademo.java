package jamesdesbyrne.gmail.com.rsademo;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by james on 24/02/15.
 * Uses
 * - Creates the activity_rsademo
 * - Allows the user to encrypt and decrypt data
 * - Allows the user to generate new keys and displays them
 */
public class activity_rsademo extends AppCompatActivity implements View.OnClickListener {

    private EditText output;
    private TextView priInt, pubInt;
    private Button encDec, newKey;
    //Used for
    // - Encrypting and Decrypting inputted text
    // - Generating and storing new keys
    private RSA rsa = new RSA();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsademo);

        // Assigns variables to there views
        output = findViewById(R.id.messageEditText);
        priInt = findViewById(R.id.textViewPriInt);
        pubInt = findViewById(R.id.textViewPubInt);

        encDec = findViewById(R.id.enc_dec_button);
        newKey = findViewById(R.id.newKeyButton);

        encDec.setOnClickListener(this);
        newKey.setOnClickListener(this);

        //Get the public and private keys
        try {
            initalKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    // Gets the new keys for the first time screen is opened
    public void initalKeys() throws NoSuchAlgorithmException {
        // Checks if the textviews already contain text
        if (priInt.getText() == null || priInt.getText() == "304535") {

            // If not it checks that keys exist
            if (rsa.getPriKey() == null || rsa.getPubKey() == null) {
                //Generates new keys if they dont exist
                rsa.genKeys();
            }
            // fills textviews with the correct Key

            priInt.setText(rsa.getPriKey());
            pubInt.setText(rsa.getPubKey());
        }
    }


    /**
     * Under construction fo the final release
     */
    public String buildString(boolean h) {
        if (h) {
            String[] splitter = rsa.getPriKey().split(getString(R.string.split1));
            String format = splitter[0];
            String mod = splitter[1];
            splitter = mod.split(",");
            mod = splitter[0];
            String exp = splitter[1];

            return format + " \n" + mod + " \n" + exp;


        } else {
            String[] splitter = rsa.getPubKey().split(getString(R.string.split1));
            String format = splitter[0];
            String mod = splitter[1];
            splitter = mod.split(",");
            mod = splitter[0];
            String exp = splitter[1];

            return format + " \n" + mod + " \n" + exp;
        }
    }


    /*
    * Handles the functionality of the buttons
    * Uses the current text of the button to determine whether to encrypt of decrypt
    * the message
    * if (Encrypt then rsa.encrypt) else vice versa
    * it then changes the text to the inverse operation Enc <-> Dec
    *
    * if the new key button is pressed it generates new keys and
    * sets the text of priString and pubString
    * */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enc_dec_button) {
            String temp = encDec.getText().toString();
            if (temp.equals("Encrypt")) {
                try {
                    output.setText(rsa.encrypt(output.getText().toString()));
                    encDec.setText("Decrypt");
                } catch (NoSuchPaddingException |
                        NoSuchAlgorithmException | InvalidKeyException |
                        BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    output.setText(rsa.decrypt());
                    encDec.setText("Encrypt");
                } catch (NoSuchPaddingException |
                        NoSuchAlgorithmException | InvalidKeyException |
                        BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
        }
        if (v.getId() == R.id.newKeyButton) {
            try {
                rsa.setSize(32);
                //Generates new Keys for displaying
                rsa.genKeys();
                //fills textviews with the correct Key
                priInt.setText(rsa.getPriKey());
                pubInt.setText(rsa.getPubKey());
                rsa.setSize(1024);
                rsa.genKeys();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}
