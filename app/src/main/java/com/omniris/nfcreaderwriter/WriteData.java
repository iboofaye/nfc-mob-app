package com.omniris.nfcreaderwriter;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WriteData extends AppCompatActivity {
    private Button btnRead;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;

    private static String TAG="WRITE_NFC";
    private NfcAdapter mNfcAdapter;

    private String writtenMessageFormat = NfcAdapter.ACTION_TAG_DISCOVERED;

    //params
    private TextView txtcardid;
    private TextView txthwtype;
    private TextView txthwtdata;

    private String trace;

    AlertDialog.Builder builder;

    private PendingIntent pendingIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportActionBar?.hide();
        setContentView(R.layout.activity_write_data);
        btnRead = findViewById(R.id.btnread);
        txthwtype = findViewById(R.id.txtmachineid);
        txtcardid = findViewById(R.id.txtshopid);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        btnRead.setOnClickListener(
                view -> startActivity(new Intent(this, MainActivity.class))
        );

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        writeTag(getIntent());
}

    /*private void writeTag (Intent intent){
        NdefMessage message = null;
        try {
            if(!txtcardid.getText().toString().equals("") && !txthwtype.getText().toString().equals("") ) {
                String cardid = txtcardid.getText().toString();
                String hwtype = txthwtype.getText().toString();
                if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
                    // Tag info
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    Toast.makeText(this, "Tag : " + tag, Toast.LENGTH_LONG).show();
                    //return;
                    Ndef ndef = Ndef.get(tag);
                    //return;
                    if (ndef.isWritable()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            message = new NdefMessage(new NdefRecord[]{
                                    NdefRecord.createTextRecord("en", cardid),
                                    NdefRecord.createTextRecord("en", hwtype)
                                    //NdefRecord.createTextRecord("en", userid)
                            }
                            );
                        }
                        ndef.connect();
                        ndef.writeNdefMessage(message);
                        ndef.close();
                        Toast.makeText(this, "Successfully Wrote!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {
                Toast.makeText(this, "Write on text box!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception Ex) {
            Toast.makeText(this, Ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null)
            //mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
            mNfcAdapter.enableForegroundDispatch(this, getPendingIntent(), getIntentFilters(), getTechLists());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    //================================================================
    private PendingIntent getPendingIntent() {
        return PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


    private IntentFilter[] getIntentFilters() {
        if(writtenMessageFormat.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            ndef.addDataScheme("http");
            return new IntentFilter[] {ndef, };
        }
        else if(writtenMessageFormat.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            IntentFilter ntech = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            return new IntentFilter[] {ntech,};
        }
        return null;
    }


    private String[][] getTechLists() {
        return new String[][] { new String[] { Ndef.class.getName()} };
    }

    public byte[] buildTextPayload(String message, String encodage, Locale langue) {
        byte[] payload;

        int codeEncodage = encodage.equals("UTF-8") ? 0 : 128;
        Charset charsetEncodage = Charset.forName(encodage);

        byte[] langByte = langue.getLanguage().getBytes(Charset.forName("US-ASCII"));
        byte[] messageByte = message.getBytes(charsetEncodage);

        payload = new byte[1 + langByte.length + messageByte.length];
        payload[0] = (byte)(codeEncodage + langByte.length);
        System.arraycopy(langByte, 0, payload, 1, langByte.length);
        System.arraycopy(messageByte, 0, payload, 1 + langByte.length, messageByte.length);

        return payload;
    }

    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }

    public void writeTag(final Intent intent) {
        NdefRecord aRecord_TnfMimeMedia;
        NdefRecord aRecord_TnfWellKnown;
        if(!txtcardid.getText().toString().equals("") && !txthwtype.getText().toString().equals("") ) {
            try{
                byte[] payload = (txtcardid.getText().toString() + " : " + txthwtype.getText().toString() + getString(R.string.ceci_est_le_contenu_du_tag_nfc)).getBytes();

                aRecord_TnfMimeMedia = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                        new byte[]{}, payload);

                aRecord_TnfWellKnown = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                        new byte[0], payload);

                NdefRecord[] records = new NdefRecord[]{aRecord_TnfWellKnown};
                final NdefMessage message = new NdefMessage(records);
                if (write(message, intent))
                    showMessage("Le Tag est bien formaté");
                else
                    showMessage("Le Tag n'est pas bien formaté");

            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Veuillez Saisir les champs!", Toast.LENGTH_SHORT).show();
        }

    }

    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean write(NdefMessage rawMessage, Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        if (ndef!=null) {
            try {
                ndef.connect();
            } catch (IOException e) {
                showMessage("Erreur � la connection du ndef");
                e.printStackTrace();
            }
            if(!ndef.isWritable()) {
                showMessage("Le tag ndef est en lecture seule");
                return false;
            }
            try {
                ndef.writeNdefMessage(rawMessage);
                showMessage("Le tag a été écrit");
            } catch (IOException e) {
                showMessage("Erreur IOException à l'écriture du ndef");
                e.printStackTrace();
            } catch (FormatException e) {
                showMessage("Erreur de format à l'écriture du ndef");
                e.printStackTrace();
                return false;
            }
            finally {
                try {
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        else { // Le tag n'est pas formaté
            NdefFormatable format = NdefFormatable.get(tag);
            if (format != null) {
                try {
                    format.connect();
                    format.format(rawMessage);

                    writeNdefSuccess();

                    return true;
                } catch (Exception e) {
                    writeNdefFailed(e);
                } finally {
                    try {
                        format.close();
                    } catch (IOException e) { // ignore
                    }
                }
            }
            return false ;
        }
    }


    private void writeNdefFailed(Exception e) {
        showMessage(getString(R.string.erreur_durant_ecriture_du_tag));
        Log.d(TAG,"Erreur ecriture tag :" + e.getMessage());
    }


    private void writeNdefSuccess() {
        showMessage(getString(R.string.un_tag_nfc_a_ete_ecrit));
    }
}
