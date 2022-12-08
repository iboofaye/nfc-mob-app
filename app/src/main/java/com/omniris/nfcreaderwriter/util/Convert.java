package com.omniris.nfcreaderwriter.util;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;


public class Convert {

    // self-defined APDU
    public static final String STATUS_SUCCESS = "9000";
    public static final String STATUS_FAILED = "6F00";
    public static final String CLA_NOT_SUPPORTED = "6E00";
    public static final String INS_NOT_SUPPORTED = "6D00";
    public static final String AID = "A0000002471001";
    public static final String LC = "07";
    public static final String SELECT_INS = "A4";
    public static final String DEFAULT_CLA = "00";

    public static String byteToHex(byte num, boolean upper) {
        char[] hexDigits = new char[2];
        if (upper) {
            hexDigits[0] = Character.toUpperCase(Character.forDigit((num >> 4) & 0xF, 16));
            hexDigits[1] = Character.toUpperCase(Character.forDigit((num & 0xF), 16));
        } else {
            hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
            hexDigits[1] = Character.forDigit((num & 0xF), 16);
        }

        return new String(hexDigits);
    }

    public static String encodeHexString(byte[] byteArray, boolean upper) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (byte aByteArray : byteArray) {
            hexStringBuffer.append(byteToHex(aByteArray, upper));
        }
        return hexStringBuffer.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
