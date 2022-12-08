package com.omniris.nfcreaderwriter.util;

public class MainAct {
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
    private  NfcAdapter nfcAdapter;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onCreate: "+ LocalDateTime.now()+"\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        btnWrite.setOnClickListener(
                view -> startActivity(new Intent(this, WriteData.class))
        );
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
            //nfc process start
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplication(), getApplicationContext().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            intentFiltersArray = new IntentFilter[]{ndef};
            if (nfcAdapter == null) {
                 builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setMessage("This device doesn't support NFC.");
                builder.setPositiveButton("Cancel", null);
                AlertDialog myDialog = builder.create();
                myDialog.setCanceledOnTouchOutside(false);
                myDialog.show();
                mNfcInfoText.setText("THIS DEVICE DOESN'T SUPPORT NFC. PLEASE TRY WITH ANOTHER DEVICE!");
                //txtviewmachineid.visibility = View.INVISIBLE;

            } else if (!nfcAdapter.isEnabled()) {
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
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onResume: "+ LocalDateTime.now()+"\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        System.out.println("pendingIntent......... : "+pendingIntent.toString());
        System.out.println("intentFiltersArray......... : "+intentFiltersArray.toString());
        System.out.println("techListsArray......... : "+techListsArray.toString());
        super.onResume();
        try {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    String iswrite = "0";
    String cardId="";
    String hwType="";
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onNewIntent: "+ LocalDateTime.now()+"\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
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



            /* with(parcelables) {
                try {
                    NdefMessage inNdefMessage = (NdefMessage) MainActivity.this[0];
                    NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
                    //if there are many records, you can call inNdefRecords[1] as array
                    NdefRecord ndefRecord_0 = inNdefRecords[0];
                    String inMessage = new String(ndefRecord_0.getPayload());
                    cardId = inMessage.substring(3);
                    //txtviewshopid.setText("Card ID: " + cardId);
                    ndefRecord_0 = inNdefRecords[1];
                    inMessage = new String(ndefRecord_0.getPayload());
                    hwType = inMessage.substring(3);
                    mNfcInfoText.setText("Card ID: " + cardId+"\n"+"HardWare Type : " + hwType);

                    if (!mNfcInfoText.getText().toString().equals("")) {
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
//
                    } else {
                        try {


                            ndefRecord_0 = inNdefRecords[2];
                            inMessage = new String(ndefRecord_0.getPayload());

                            mNfcInfoText.setText("Card ID: " + inMessage.substring(3));
                        }
                        catch (Exception ex){
                            Toast.makeText(this, "Hardware Type not written!", Toast.LENGTH_SHORT).show();
                        }
                    }
} catch (Exception ex) {
        Toast.makeText(this, "There are no information found!, please click write data to write !", Toast.LENGTH_SHORT).show();
        }
        }

    @Override
    protected void onPause() {
            if (this.isFinishing()) {
            nfcAdapter.disableForegroundDispatch(this);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace += "onPause: "+ LocalDateTime.now()+"\n";
            mNfcInfoText.setText(trace);
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
            }
            super.onPause();
            }
            }
     */
}
