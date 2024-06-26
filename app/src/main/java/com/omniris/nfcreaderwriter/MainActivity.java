/*
package com.omniris.nfcreaderwriter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.omniris.nfcreaderwriter.util.Convert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private IntentFilter[] intentFiltersArray = null;
    //private final String[] nfcfListe = new String[]{String.valueOf(NfcF.class)};
    private final String[][] techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
    private String trace = "";
    public String abc = NfcF.class.getName();
    private NfcAdapter nfcAdapter;
    private TextView Tv;
    private ActionBar actionBar;
    private Button btnRead;
    private Button btnWrite;
    private TextView mNfcInfoText;
    private PendingIntent pendingIntent = null;

    private AlertDialog.Builder builder;

    public synchronized NfcAdapter getNfcAdapter() {
        if (nfcAdapter == null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        }
        return nfcAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //boolean supportActionBar;
        //supportActionBar?.hide();
        setContentView(R.layout.activity_main);
        btnWrite = findViewById(R.id.btnwrite);
        mNfcInfoText = findViewById(R.id.txtviewsdata);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcInfoText.setMovementMethod(new ScrollingMovementMethod());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onCreate: " + LocalDateTime.now() + "\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        btnWrite.setOnClickListener(
                view -> startActivity(new Intent(this, WriteData.class))
        );

        if (nfcAdapter == null) {
            builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("This device doesn't support NFC.");
            builder.setPositiveButton("Cancel", null);
            AlertDialog myDialog = builder.create();
            myDialog.setCanceledOnTouchOutside(false);
            myDialog.show();
            mNfcInfoText.setText("THIS DEVICE DOESN'T SUPPORT NFC. PLEASE TRY WITH ANOTHER DEVICE!");
            //txtviewmachineid.visibility = View.INVISIBLE;

        }
        else {
            if (!nfcAdapter.isEnabled()) {
                builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setTitle("NFC Disabled");
                builder.setMessage("Plesae Enable NFC");
                mNfcInfoText.setText("NFC IS NOT ENABLED. PLEASE ENABLE NFC IN SETTINGS->NFC");
                //txtviewmachineid.visibility = View.INVISIBLE

                builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog myDialog = builder.create();
                myDialog.setCanceledOnTouchOutside(false);
                myDialog.show();
            }
            else{
                IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                try {
                    ndef.addDataType("text/plain");
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                //nfc process start
                pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        0,
                        new Intent(
                                getApplication(),
                                getApplicationContext().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        0);
                intentFiltersArray = new IntentFilter[]{ndef};
            }
        }
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onResume: " + LocalDateTime.now() + "\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        super.onResume();
        try {
            nfcAdapter.enableForegroundDispatch(
                    this,
                    pendingIntent,
                    intentFiltersArray,
                    techListsArray);
        } catch (Exception ex) {
            Toast.makeText(this,ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onNewIntent: " + LocalDateTime.now() + "\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        //resolveIntent(intent);
    }

    @Override
    protected void onPause() {
        if (this.isFinishing()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onPause: " + LocalDateTime.now() + "\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        super.onPause();
    }





    public void processIntent(Intent intent) {
        try {
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                Toast.makeText(this, "ACTION_TECH_DISCOVERED", Toast.LENGTH_LONG).show();
            }
            if (Intent.ACTION_MAIN.equals(intent.getAction())) {
                Toast.makeText(this, "ACTION_MAIN - Matched", Toast.LENGTH_LONG).show();
                //return;
            } else {
                Toast.makeText(this, "Invalid action - " + intent.getAction(), Toast.LENGTH_LONG).show();
                //return;
            }
            StringBuilder nfcInfo = new StringBuilder();
            byte[] extraId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

            // Id
            if (extraId != null) {
                nfcInfo.append("ID (hex): ").append(Convert.encodeHexString(extraId, false)).append("\n");
                Toast.makeText(this, "ID (hex): " + Convert.encodeHexString(extraId, false), Toast.LENGTH_LONG).show();
            }

            // Tag info
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Toast.makeText(this, "Tag : " + tag, Toast.LENGTH_LONG).show();

            // Technologies
            StringBuilder technologiesAvailable = new StringBuilder("Technologies Available: \n");

            // Card type.
            StringBuilder cardType = new StringBuilder("Card Type: \n");

            // Sector and block.
            StringBuilder sectorAndBlock = new StringBuilder("Storage: \n");

            // Sector check
            StringBuilder sectorCheck = new StringBuilder("Sector check: \n");

            int idx = 0;
            for (String tech : tag.getTechList()) {
                if (tech.equals(MifareClassic.class.getName())) {
                    // Mifare Classic
                    MifareClassic mfc = MifareClassic.get(tag);
                    switch (mfc.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            cardType.append("Classic");
                            break;

                        case MifareClassic.TYPE_PLUS:
                            cardType.append("Plus");
                            break;

                        case MifareClassic.TYPE_PRO:
                            cardType.append("Pro");
                            break;

                        case MifareClassic.TYPE_UNKNOWN:
                            cardType.append("Unknown");
                            break;
                    }

                    sectorAndBlock.append("Sectors: ").append(mfc.getSectorCount()).append("\n")
                            .append("Blocks: ").append(mfc.getBlockCount()).append("\n")
                            .append("Size: ").append(mfc.getSize()).append(" Bytes");

                    try {
                        // Enable I/O to the tag
                        mfc.connect();

                        for (int i = 0; i < mfc.getSectorCount(); ++i) {
                            if (mfc.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT)) {
                                sectorCheck.append("Sector <").append(i).append("> with KeyA auth succ\n");

                                // Read block of sector
                                final int blockIndex = mfc.sectorToBlock(i);
                                for (int j = 0; j < mfc.getBlockCountInSector(i); ++j) {
                                    byte[] blockData = mfc.readBlock(blockIndex + j);
                                    sectorCheck.append("  Block <").append(blockIndex + j).append("> ")
                                            .append(Convert.encodeHexString(blockData, false)).append("\n");
                                }

                            } else if (mfc.authenticateSectorWithKeyB(i, MifareClassic.KEY_DEFAULT)) {
                                sectorCheck.append("Sector <").append(i).append("> with KeyB auth succ\n");

                                // Read block of sector
                                final int blockIndex = mfc.sectorToBlock(i);
                                for (int j = 0; j < mfc.getBlockCountInSector(i); ++j) {
                                    byte[] blockData = mfc.readBlock(blockIndex + j);
                                    sectorCheck.append("  Block <").append(blockIndex + j).append("> ")
                                            .append(Convert.encodeHexString(blockData, false)).append("\n");
                                }
                            } else {
                                sectorCheck.append("Sector <").append(i).append("> auth failed\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Try again and keep NFC tag below device", Toast.LENGTH_LONG).show();
                    }
                } else if (tech.equals(MifareUltralight.class.getName())) {
                    // Mifare Ultralight
                    MifareUltralight mful = MifareUltralight.get(tag);
                    switch (mful.getType()) {
                        case MifareUltralight.TYPE_ULTRALIGHT:
                            cardType.append("Ultralight");
                            break;

                        case MifareUltralight.TYPE_ULTRALIGHT_C:
                            cardType.append("Ultralight C");
                            break;

                        case MifareUltralight.TYPE_UNKNOWN:
                            cardType.append("Unknown");
                            break;
                    }
                }

                String[] techPkgFields = tech.split("\\.");
                if (techPkgFields.length > 0) {
                    final String techName = techPkgFields[techPkgFields.length - 1];
                    if (0 == idx++) {
                        technologiesAvailable.append(techName);
                    } else {
                        technologiesAvailable.append(", ").append(techName);
                    }
                }
            }

            nfcInfo.append("\n").append(technologiesAvailable).append("\n").append("\n").append(cardType).append("\n");
            // Getting NDEF Message by this process
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            // NDEF Messages
            StringBuilder sbNdefMessages = new StringBuilder("NDEF Messages: \n");
            //Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null) {
                Toast.makeText(MainActivity.this, "Raw Message Not null: ", Toast.LENGTH_LONG).show();
                NdefMessage[] messages = new NdefMessage[parcelables.length];
                for (int i = 0; i < parcelables.length; ++i) {
                    messages[i] = (NdefMessage) parcelables[i];
                }
                for (NdefMessage message : messages) {
                    for (NdefRecord record : message.getRecords()) {
                        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                                try {
                                    // NFC Forum "Text Record Type Definition" section 3.2.1.
                                    byte[] payload = record.getPayload();
                                    String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                                    int languageCodeLength = payload[0] & 0077;
                                    String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                                    String text = new String(payload, languageCodeLength + 1,
                                            payload.length - languageCodeLength - 1, textEncoding);
                                    sbNdefMessages.append(" - ").append(languageCode).append(", ")
                                            .append(textEncoding).append(", ").append(text).append("\n");
                                } catch (UnsupportedEncodingException e) {
                                    // should never happen unless we get a malformed tag.
                                    //throw new IllegalArgumentException(e);
                                    Toast.makeText(this, "Tag Format  : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
            }
            nfcInfo.append("\n").append(sbNdefMessages).append("\n")
                    .append("\n").append(sectorAndBlock).append("\n")
                    .append("\n").append(sectorCheck).append("\n");
            Toast.makeText(this, "SbNdefMessage : " + sbNdefMessages, Toast.LENGTH_LONG).show();

            mNfcInfoText.setText(nfcInfo.toString());



            */
/* with(parcelables) {
             *//*

        } catch (Exception ex) {
            Toast.makeText(this, "There are no information found!, please click write data to write !", Toast.LENGTH_SHORT).show();
        }
    }

    public void resolveIntent(Intent intent) {
        try {

        */
/*
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.getAction() || NfcAdapter.ACTION_NDEF_DISCOVERED == intent.getAction()) {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    //return;
                    Ndef ndef = Ndef.get(tag);
                    //return;
                    if (ndef.isWritable()) {

                        NdefMessage message = new NdefMessage(new NdefRecord[]{
                                NdefRecord.createTextRecord("en", cardId),
                                NdefRecord.createTextRecord("en", hwType)
                                //NdefRecord.createTextRecord("en", txtuserid.text.toString())
                        }
                        );
                        ndef.connect();
                        ndef.writeNdefMessage(message);
                        ndef.close();
                        mNfcInfoText.setText("Card ID: "+mNfcInfoText.getText());
                        Toast.makeText(this, "Successfully Wrote!", Toast.LENGTH_SHORT).show();
                    }
                }
        *//*

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            // NDEF Messages
            StringBuilder sbNdefMessages = new StringBuilder("NDEF Messages: \n");
            //Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null) {
                Toast.makeText(MainActivity.this, "Raw Message Not null: ", Toast.LENGTH_LONG).show();
                NdefMessage[] messages = new NdefMessage[parcelables.length];
                for (int i = 0; i < parcelables.length; ++i) {
                    messages[i] = (NdefMessage) parcelables[i];
                }
                for (NdefMessage message : messages) {
                    for (NdefRecord record : message.getRecords()) {
                        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                                // NFC Forum "Text Record Type Definition" section 3.2.1.
                                byte[] payload = record.getPayload();
                                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                                int languageCodeLength = payload[0] & 0077;
                                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                                String text = new String(payload, languageCodeLength + 1,
                                        payload.length - languageCodeLength - 1, textEncoding);
                                sbNdefMessages.append(" - ").append(languageCode).append(", ")
                                        .append(textEncoding).append(", ").append(text).append("\n");
                                // should never happen unless we get a malformed tag.
                                //throw new IllegalArgumentException(e);
                                //Toast.makeText(this, "Tag Format  : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                mNfcInfoText.setText(sbNdefMessages.toString());
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Hardware Data not Readable!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}*/

package com.omniris.nfcreaderwriter;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.omniris.nfcreaderwriter.util.Convert;
import com.omniris.nfcreaderwriter.util.TagMessages;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    private TextView mTextView;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button ecrireTag = (Button)findViewById(R.id.btnwrite);
        ecrireTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,WriteData.class));
            }
        });

        mTextView = (TextView) findViewById(R.id.txtviewsdata);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            mTextView.setText("Lire un Tag NFC ...");
            Toast.makeText(this, TagMessages.READ_SUCCESS, Toast.LENGTH_LONG).show();
        } else {
            mTextView.setText("Ce telephone n'a pas de support NFC.");
            Toast.makeText(this, "Ce telephone n'a pas de support NFC.", Toast.LENGTH_LONG).show();
        }

        // create an intent with tag data and deliver to this activity
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), getApplicationContext().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techIntent = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[]{ndefIntent, techIntent};
        } catch (Exception e) {
            Log.e("TagDispatch", e.toString());
        }

        mNFCTechLists = new String[][]{new String[]{
                NfcF.class.getName()
        }
        };

        resolveIntent(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        resolveIntent(intent);

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);
    }

    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                mTextView.setText(mTextView.getText() + "\n" + message);
            }
        });
    }
  /*  private void newResolveIntent(Intent intent) {
        if(intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)
                || intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)
                || intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if(rawMsgs != null){
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

//            for(NdefMessage ndefMessage: msgs) {
//                NdefRecord[] records = ndefMessage.getRecords();
//                for(NdefRecord record : records) {
//                    try {
//                        if(record.getTnf()==NdefRecord.TNF_ABSOLUTE_URI)
//                            showMessage("Tag de typeTNF_ABSOLUTE_URI" );
//                        if(record.getTnf()==NdefRecord.TNF_EMPTY)
//                            showMessage("Tag de typeTNF_EMPTY" );
//                        if(record.getTnf()==NdefRecord.TNF_EXTERNAL_TYPE)
//                            showMessage("Tag de TNF_EXTERNAL_TYPE" );
//                        if(record.getTnf()==NdefRecord.TNF_MIME_MEDIA)
//                            showMessage("Tag de TNF_MIME_MEDIA" );
//                        if(record.getTnf()==NdefRecord.TNF_UNCHANGED)
//                            showMessage("Tag de TNF_UNCHANGED" );
//                        if(record.getTnf()==NdefRecord.TNF_UNKNOWN)
//                            showMessage("Tag de TNF_UNKNOWN" );
//                        if(record.getTnf()==NdefRecord.TNF_WELL_KNOWN) {
//                            showMessage("Tag de TNF_WELL_KNOWN" );
//
//                            if(Arrays.equals(record.getType(),NdefRecord.RTD_ALTERNATIVE_CARRIER))
//                                showMessage("getType de RTD_ALTERNATIVE_CARRIER" );
//
//                            if(Arrays.equals(record.getType(),NdefRecord.RTD_HANDOVER_CARRIER))
//                                showMessage("getType de RTD_HANDOVER_CARRIER" );
//
//                            if(Arrays.equals(record.getType(),NdefRecord.RTD_HANDOVER_REQUEST))
//                                showMessage("getType de RTD_HANDOVER_REQUEST" );
//
//                            if(Arrays.equals(record.getType(),NdefRecord.RTD_HANDOVER_SELECT))
//                                showMessage("getType de RTD_HANDOVER_SELECT" );
//
//                            if(Arrays.equals(record.getType(),NdefRecord.RTD_SMART_POSTER))
//                                showMessage("getType de RTD_SMART_POSTER" );
//
//                            if(Arrays.equals(record.getType(),NdefRecord.RTD_TEXT)) {
//                                showMessage("getType de RTD_TEXT" );
//
//                            }
//                            if(Arrays.equals(record.getType(),NdefRecord.RTD_URI)) {
//                                showMessage("getType de RTD_URI" );
//                            }
//                        }
//
//                        showMessage(new String( record.getPayload(), "UTF-8"));
//                        Log.d(TAG,"lecture :" + new String(record.getPayload(), "UTF-8"));
//
//                    } catch (UnsupportedEncodingException e) {
//                        showMessage("Erreur à la lecture du payload");
//                        e.printStackTrace();
//                    }
//                }
            }
             else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_DATA);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }
            // Setup the views
            for (int i = 0; i < msgs.length; i++) {
                extractMessage(msgs[i]);
            }
        }

            *//*else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }*//*
        }*/
//    private void perform(Intent intent)
//    {
//        mTextView.setText(mTextView.getText() + "\n" + "onNewIntent");
//        String action = intent.getAction();
//        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//        String s = action + "nn" + tag.toString() + "ww";
//
//        // parse through all NDEF messages and their records and pick text type only
//        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//        if (data != null) {
//            try {
//                for (int i = 0; i < data.length; i++) {
//                    NdefRecord[] recs = ((NdefMessage)data[i]).getRecords();
//                    for (int j = 0; j < recs.length; j++) {
//                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
//                                Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
//                            byte[] payload = recs[j].getPayload();
//                            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
//                            int langCodeLen = payload[0] & 0077;
//
//                            s += ("nnNdefMessage[" + i + "], NdefRecord[" + j + "]:n" +
//                            new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,
//                                    textEncoding) + "zz");
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("TagDispatch", e.toString());
//            }
//        }
//
//        mTextView.setText(s);
//    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        NdefMessage[] msgs = new NdefMessage[0];
        //mTextView.setText(mTextView.getText() + "\n" + action);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                Toast.makeText(this, "Tag inconnu !.", Toast.LENGTH_LONG).show();
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                if(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)==null) {
                    Toast.makeText(this, "Tag Vide !.", Toast.LENGTH_LONG).show();
                }
                else {
                    // Tag info
                    Tag tagtech = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    for (String tech : tagtech.getTechList()) {
                        if (tech.equals(MifareClassic.class.getName())) {
                            showMessage("Technology : "+tech);
                        }
                    }
                    Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    byte[] payload = dumpTagData(tag).getBytes();
                    NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                    NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                    msgs = new NdefMessage[]{msg};
                }
            }
            // Setup the views
            for (int i = 0; i < msgs.length; i++) {
                extractMessage(msgs[i]);
            }
        }
    }

    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        return String.valueOf(getDec(id));
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

//    private void resolveIntent(Intent intent)
//    {
//        String action = intent.getAction();
//        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) ||
//                (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)))
//        {
//            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//            Parcelable[] rawMsgs = intent
//                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage msg = (NdefMessage) rawMsgs[0];
//
//            extractMessage(msg);
//
//        }
//    }

    private void extractMessage(NdefMessage msg) {
        byte[] array = null;
        byte[] array2 = null;
        String displaystring = "";
        String displaystring2 = "";

        for (int i = 0; i <= msg.getRecords().length; i++){
            array = msg.getRecords()[0].getPayload();
            if (array != null) {
                displaystring += "\n" + "PAYLOAD : " + convert(array);
            }
            array = msg.getRecords()[0].getId();
            if (array != null) {
                displaystring += "\n" + "ID : " + convert(array);
            }
            array = msg.getRecords()[0].getType();
            if (array != null) {
                displaystring += "\n" + "TYPE : " + convert(array);
            }
            showMessage(displaystring);
        }

    }

    String convert(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length);
        for (int i = 0; i < data.length; ++i) {
            if (data[i] < 0) throw new IllegalArgumentException();
            sb.append((char) data[i]);
        }
        return sb.toString();
    }

    String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        ;

        return sb.toString();
    }

    @Override
    public void onResume() {
        super.onResume();

        //mTextView.setText(mTextView.getText() + "\n" + "onResume");

        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();

        //mTextView.setText(mTextView.getText() + "\n" + "onPause");

        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    /*private void processResolveIntent(Intent intent) {

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Toast.makeText(this, "ACTION_TECH_DISCOVERED", Toast.LENGTH_LONG).show();
        } else if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            return;
        } else {
            Toast.makeText(this, "Invalid action - " + intent.getAction(), Toast.LENGTH_LONG).show();
            return;
        }

        //StringBuilder nfcInfo = new StringBuilder();
        byte[] extraId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

        // Id
        if (extraId != null) {
            showMessage(Convert.encodeHexString(extraId, false));
        }

        // Tag info
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // Technologies
        StringBuilder technologiesAvailable = new StringBuilder("Technologies Available: \n");

        // Card type.
        StringBuilder cardType = new StringBuilder("Card Type: \n");

        // Sector and block.
        StringBuilder sectorAndBlock = new StringBuilder("Storage: \n");

        // Sector check
        StringBuilder sectorCheck = new StringBuilder("Sector check: \n");

        int idx = 0;
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                // Mifare Classic
                MifareClassic mfc = MifareClassic.get(tag);
                switch (mfc.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        cardType.append("Classic");
                        break;

                    case MifareClassic.TYPE_PLUS:
                        cardType.append("Plus");
                        break;

                    case MifareClassic.TYPE_PRO:
                        cardType.append("Pro");
                        break;

                    case MifareClassic.TYPE_UNKNOWN:
                        cardType.append("Unknown");
                        break;
                }

                sectorAndBlock.append("Sectors: ").append(mfc.getSectorCount()).append("\n")
                        .append("Blocks: ").append(mfc.getBlockCount()).append("\n")
                        .append("Size: ").append(mfc.getSize()).append(" Bytes");

                try {
                    // Enable I/O to the tag
                    mfc.connect();

                    for (int i = 0; i < mfc.getSectorCount(); ++i) {
                        if (mfc.authenticateSectorWithKeyA(i, MifareClassic.KEY_DEFAULT)) {
                            sectorCheck.append("Sector <").append(i).append("> with KeyA auth succ\n");

                            // Read block of sector
                            final int blockIndex = mfc.sectorToBlock(i);
                            for (int j = 0; j < mfc.getBlockCountInSector(i); ++j) {
                                byte[] blockData = mfc.readBlock(blockIndex + j);
                                sectorCheck.append("  Block <").append(blockIndex + j).append("> ")
                                        .append(Convert.encodeHexString(blockData, false)).append("\n");
                            }

                        } else if (mfc.authenticateSectorWithKeyB(i, MifareClassic.KEY_DEFAULT)) {
                            sectorCheck.append("Sector <").append(i).append("> with KeyB auth succ\n");

                            // Read block of sector
                            final int blockIndex = mfc.sectorToBlock(i);
                            for (int j = 0; j < mfc.getBlockCountInSector(i); ++j) {
                                byte[] blockData = mfc.readBlock(blockIndex + j);
                                sectorCheck.append("  Block <").append(blockIndex + j).append("> ")
                                        .append(Convert.encodeHexString(blockData, false)).append("\n");
                            }
                        } else {
                            sectorCheck.append("Sector <").append(i).append("> auth failed\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Try again and keep NFC tag below device", Toast.LENGTH_LONG).show();
                }
            } else if (tech.equals(MifareUltralight.class.getName())) {
                // Mifare Ultralight
                MifareUltralight mful = MifareUltralight.get(tag);
                switch (mful.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        cardType.append("Ultralight");
                        break;

                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        cardType.append("Ultralight C");
                        break;

                    case MifareUltralight.TYPE_UNKNOWN:
                        cardType.append("Unknown");
                        break;
                }
            }

            String[] techPkgFields = tech.split("\\.");
            if (techPkgFields.length > 0) {
                final String techName = techPkgFields[techPkgFields.length - 1];
                if (0 == idx++) {
                    technologiesAvailable.append(techName);
                } else {
                    technologiesAvailable.append(", ").append(techName);
                }
            }
        }

        showMessage(String.valueOf(technologiesAvailable));

        // NDEF Messages
        StringBuilder sbNdefMessages = new StringBuilder("NDEF Messages: \n");
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; ++i) {
                messages[i] = (NdefMessage) rawMessages[i];
            }

            for (NdefMessage message : messages) {
                for (NdefRecord record : message.getRecords()) {
                    if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
                        if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                            try {
                                // NFC Forum "Text Record Type Definition" section 3.2.1.
                                byte[] payload = record.getPayload();
                                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                                int languageCodeLength = payload[0] & 0077;
                                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                                String text = new String(payload, languageCodeLength + 1,
                                        payload.length - languageCodeLength - 1, textEncoding);
                                sbNdefMessages.append(" - ").append(languageCode).append(", ")
                                        .append(textEncoding).append(", ").append(text).append("\n");
                            } catch (UnsupportedEncodingException e) {
                                // should never happen unless we get a malformed tag.
                                throw new IllegalArgumentException(e);
                            }
                        }
                    }
                }
            }
        }
        showMessage(String.valueOf(sbNdefMessages));
        showMessage(String.valueOf(sectorAndBlock));
        showMessage(String.valueOf(sectorCheck));

        //showMessage(nfcInfo.toString());
    }*/

}
