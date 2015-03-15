package jamesdesbyrne.gmail.com.rsademo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
public class activity_rsademo extends ActionBarActivity implements  View.OnClickListener {

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


        /**
         * ERROR:
         * WHEN THE SCREEN IS SWITCHED BACK THE
         * NEW KEYS THAT WHERE GENERATED ARE REPLACED
         * BY THE EXISTING STRINGS IN STINGS.XML
         */

        // Assigns variables to there views
        output = (EditText)findViewById(R.id.messageEditText);
        priInt = (TextView)findViewById(R.id.textViewPriInt);
        pubInt = (TextView)findViewById(R.id.textViewPubInt);

        encDec = (Button)findViewById(R.id.enc_dec_button);
        newKey = (Button)findViewById(R.id.newKeyButton);

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
        if (priInt.getText() == null || priInt.getText() == "304535" ){

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_explanation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();

        if (id == R.id.action_explanation){
            Intent intent = new Intent(this, activity_explanation.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

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
        if (v.getId() == R.id.enc_dec_button){
            String temp = encDec.getText().toString();
            if (temp.equals("Encrypt")){
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
        if (v.getId() == R.id.newKeyButton){
            try {
                //Generates new Keys
                rsa.genKeys();
                //fills textviews with the correct Key
                priInt.setText(rsa.getPriKey());
                pubInt.setText(rsa.getPubKey());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}
