package jamesdesbyrne.gmail.com.rsademo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
public class activity_rsademo extends ActionBarActivity implements ActionBar.OnNavigationListener, View.OnClickListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

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

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.

        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[]{
                                getString(R.string.title_section1),
                                getString(R.string.title_section2),
                                getString(R.string.title_section3),
                        }),
                this);

    }

    //Gets the new keys for the first time screen is opened
    public void initalKeys() throws NoSuchAlgorithmException {
        //Checks if the textviews already contain text
        if (priInt.getText() == null || pubInt.getText() == null ){

            //If not it checks that keys exist
            if (rsa.getPriKey() == null || rsa.getPubKey() == null) {
                //Generates new keys if they dont exist
                rsa.genKeys();
            }
            //fills textviews with the correct Key
            priInt.setText(rsa.getPriKey());
            pubInt.setText(rsa.getPubKey());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_explanation, menu);
        getMenuInflater().inflate(R.menu.menu_activity_rsademo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        return true;
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
                    output.setText(rsa.decrypt(output.getText().toString()));
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_activity_explanation, container, false);
            return inflater.inflate(R.layout.fragment_activity_explanation, container, false);
        }
    }
}
