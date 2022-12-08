package com.omniris.nfcreaderwriter.util;

public class WrtD {

    /*

    package com.omniris.nfcreaderwriter;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class WriteData extends AppCompatActivity {
    private Button btnRead;
    private IntentFilter[] intentFiltersArray = null;
    private final String[] nfcfListe = new String[]{String.valueOf(NfcF.class)};
    private final String[][] techListsArray = new String[][]{nfcfListe};

    private  NfcAdapter nfcAdapter;

    //params
    private TextView txtcardid;
    private TextView txthwtype;

    private String trace;

    AlertDialog.Builder builder;

    private PendingIntent pendingIntent = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportActionBar?.hide();
        setContentView(R.layout.activity_write_data);
        btnRead = findViewById(R.id.btnread);
        txthwtype = findViewById(R.id.txtmachineid);
        txtcardid = findViewById(R.id.txtshopid);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace = "onCreate: "+ LocalDateTime.now();
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        btnRead.setOnClickListener(
                view -> startActivity(new Intent(this, MainActivity.class))
        );
        //nfc process start
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef};
        if (nfcAdapter == null) {
            builder = new AlertDialog.Builder(WriteData.this, R.style.MyAlertDialogStyle);
            builder.setMessage("This device doesn't support NFC.");
            builder.setPositiveButton("Cancel", null);
            AlertDialog myDialog = builder.create();
            myDialog.setCanceledOnTouchOutside(false);
            myDialog.show();
            // txttext.setText("THIS DEVICE DOESN'T SUPPORT NFC. PLEASE TRY WITH ANOTHER DEVICE!")
        } else if (!nfcAdapter.isEnabled()) {
            builder = new AlertDialog.Builder(WriteData.this, R.style.MyAlertDialogStyle);
            builder.setTitle("NFC Disabled");
            builder.setMessage("Plesae Enable NFC");
            // txttext.setText("NFC IS NOT ENABLED. PLEASE ENABLE NFC IN SETTINGS->NFC")
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
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace = "onResume: "+ LocalDateTime.now();
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        try {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace = "onNewIntent: "+ LocalDateTime.now();
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        super.onNewIntent(intent);
        NdefMessage message;
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
                        message = new NdefMessage(new NdefRecord[]{
                                NdefRecord.createTextRecord("en", cardid),
                                NdefRecord.createTextRecord("en", hwtype)
                                //NdefRecord.createTextRecord("en", userid)
                        }
                        );
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
}

    @Override
    protected void onPause() {
        if (this.isFinishing()) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trace = "onResume: "+ LocalDateTime.now();
            Toast.makeText(this, trace, Toast.LENGTH_LONG).show();
        }
        super.onPause();
    }
}

     */
}
