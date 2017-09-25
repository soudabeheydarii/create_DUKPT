package com.company;

/**
 * Created by Soudabeh on 8/29/2017.
 */


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.math.BigInteger;
import java.security.InvalidParameterException;

public final class ComputeDukpt {




     // Computes a DUKPT (Derived Unique Key-Per-Transaction).
     // bbdk The Base Derivation Key
     //bksn The Key Serial Number
     //return  A unique key for this set of data.


    public static String[] computeKey(byte[] bbdk, byte[] bksn) throws Exception {

        BitSet ksn,key,bdk,ipek;
        byte[] keyipek,rkey;

        ksn = toBitSet(bksn);  // convert  array of  bytes to  Bitset
        bdk = toBitSet(bbdk);
        ipek = computeIpek(bdk, ksn);
        keyipek = toByteArray(ipek);


        key = computePek(ipek, ksn);
        rkey = toByteArray(key);  // convert  bitset to arayei of bytes



        System.out.println("IPEK : "+toHex(keyipek));
        System.out.println("PEK : "+toHex(rkey));

        String[] array= new String[2];
        array[0]= toHex(keyipek);
        array[1]= toHex(rkey);
        return array;
    }


     // Computes the Initial PIN Encryption Key
     // This algorithm was found in Annex A, on page 69
     //key The Base Derivation Key.
     //ksn The Key Serial Number.
     // return The Initial PIN Encryption Key



    public static BitSet computeIpek(BitSet key, BitSet ksn) throws Exception {

        BitSet keyRegister,data,sHexString,sipek;
        byte[][] ipek;
        byte[] bkeyRegister0,bdata0,bHexString,bdata1, bkeyRegister1,bipek;
        String HexString;


        ipek = new byte[2][];
        keyRegister = key.get(0, key.length()); // keyRegister is temp for bdk
        data = ksn.get(0, ksn.length());  //  data is temp for ksn
        data.clear(59, 80);

        bkeyRegister0 =toByteArray(keyRegister);
        bdata0 = toByteArray(data.get(0, 64)); // take the 8 MSB of this 10 byte register
        ipek[0] = encryptTripleDes(bkeyRegister0, bdata0); // and encrypt these 8 bytes using TDEA
//_____________________________

        HexString = "C0C0C0C000000000C0C0C0C000000000";
        bHexString = toByteArray(HexString);
        sHexString = toBitSet(bHexString);
        keyRegister.xor(sHexString);  //  bdk xored with this HexString
//___________________________________

        bkeyRegister1 =toByteArray(keyRegister);
        bdata1 = toByteArray(data.get(0, 64));
        ipek[1] = encryptTripleDes(bkeyRegister1, bdata1); // encrypt new bdk using TDEA goes to ipek[1]
//___________________________________
        bipek = concat(ipek[0], ipek[1]); // concat two ipek
        sipek = toBitSet(bipek);

        return sipek;
    }


     //	This algorithm was found in Annex A, on pages 50-54
    // ipek The Initial PIN Encryption Key.
    // ksn The Key Serial Number.
    //return The Dukpt


    private static BitSet computePek(BitSet ipek, BitSet ksn) throws Exception {
        int i=59;
        BitSet temp,key,counter,sHexString1;
        byte[] bHexString1 ;
        String HexString1 ;


        key = ipek.get(0, ipek.length());
        counter = ksn.get(0, ksn.length());
        counter.clear(59, ksn.length());

       // This loop is repeated as much as a counter.
        while(i < ksn.length())
        {
            if (ksn.get(i)) {
                counter.set(i);
                temp = generateKey(key, counter.get(16, 80)); // call nonreversiblekeyGenerationProcess
                key = temp;
            }
            i++;
        }


        //******************************

        HexString1 = "00000000000000000000000000000000";
        bHexString1 = toByteArray(HexString1);
        sHexString1 = toBitSet(bHexString1);
        key.xor(sHexString1); // key xored with this string

        return key;
    }




      //Creates a new key from a previous key and the right 64 bits of the
     // Key Serial Number transaction.
    //  This algorithm was found in Annex A, on page 50
    // p_key The previous key to be used for derivation.
     // data The data to encrypt it with, usually the right 64 bits of the transaction counter.
     //return A key

     private static BitSet generateKey(BitSet p_key, BitSet data) throws Exception { // nonreversiblekeyGenerationProcess
        BitSet keyreg,reg1,reg2,reg3,rkey;
        byte[] bkey1,bkey2,bkey3,bkey4,bkey5,bkey6,bkey7,reg1b,reg2b,key;



        keyreg = p_key.get(0, p_key.length());
        reg1 = data.get(0, data.length());


        reg2 = reg1.get(0, 64);
        reg2.xor(keyreg.get(64, 128));

        bkey1 = toByteArray(keyreg.get(0, 64));
        bkey2 = toByteArray(reg2);
        bkey3=encryptDes(bkey1,bkey2);
        reg2 = toBitSet(bkey3);


        reg2.xor(keyreg.get(64, 128)); // reg2 xored with right half of IPEK

//_________________________________


        bkey4 = toByteArray("C0C0C0C000000000C0C0C0C000000000");
        reg3 = toBitSet(bkey4);
        keyreg. xor(reg3);
        reg1.xor(keyreg.get(64, 128));

        //_________________________________________

        bkey5 = toByteArray(keyreg.get(0, 64));
        bkey6 = toByteArray(reg1);
        bkey7=encryptDes(bkey5,bkey6);
        reg1 = toBitSet(bkey7);

        reg1.xor(keyreg.get(64, 128));


        reg1b = toByteArray(reg1);
        reg2b = toByteArray(reg2);
        key = concat(reg1b, reg2b);
        rkey = toBitSet(key);


        return rkey;
    }


//======================================================================================================================


     // Performs Single DES Encryption.

     // key The key for encryption.
     // data The data to encrypt.
    //return The encrypted.




    public static byte[] encryptTripleDes(byte[] key, byte[] data) throws Exception {

        BitSet numkey1, numkey2, numkey3,bskey;
        byte[] bnumkey1,bnumkey2,bnumkey3,bnumkey4,bnumkey5,bnumkey6;

        IvParameterSpec iv;
        DESedeKeySpec spec;
        SecretKeyFactory keyFactory;
        SecretKey encryptKey;
        Cipher encryptor;


        bskey = toBitSet(key);
        if (bskey.length()==192){
            // triple length
            if (bskey.length() != 192) {
                throw new InvalidParameterException("Key is not 64/128/192 bits long.");
            }
            numkey1 = bskey.get(0, 64);
            numkey2 = bskey.get(64, 128);
            numkey3 = bskey.get(128, 192);
        }
        else if (bskey.length() == 128) {
            // double length
            numkey1 = bskey.get(0, 64);
            numkey2 = bskey.get(64, 128);
            numkey3 = bskey.get(0, 64);}
        else {
            // single length
            numkey1 = bskey.get(0, 64);
            numkey2 = bskey.get(0, 64);
            numkey3 = bskey.get(0, 64);}




        bnumkey1 = toByteArray(numkey1);
        bnumkey2 = toByteArray(numkey2);
        bnumkey3 = toByteArray(numkey3);
        bnumkey4 = concat(bnumkey1, bnumkey2);
        bnumkey5 = concat(bnumkey4, bnumkey3);

        iv = new IvParameterSpec(new byte[8]);
        spec = new DESedeKeySpec(bnumkey5);

        keyFactory = SecretKeyFactory.getInstance("DESede");
        encryptKey = keyFactory.generateSecret(spec);


        encryptor = Cipher.getInstance("DESede/CBC/NoPadding");
        encryptor.init(Cipher.ENCRYPT_MODE, encryptKey, iv);
        return bnumkey6 = encryptor.doFinal(data);



    }


    public static byte[] encryptDes(byte[] key, byte[] data) throws Exception {

        IvParameterSpec iv;
        DESKeySpec spec;
        SecretKeyFactory keyFactory;
        SecretKey encryptKey;
        Cipher encryptor;



        iv = new IvParameterSpec(new byte[8]);

        spec = new DESKeySpec(key);

        keyFactory = SecretKeyFactory.getInstance("DES");

        encryptKey = keyFactory.generateSecret(spec);

        encryptor = Cipher.getInstance("DES/CBC/NoPadding");

        encryptor.init(Cipher.ENCRYPT_MODE, encryptKey, iv );

        return encryptor.doFinal(data);
    }





     // key The key for encryption.
     //data The data to encrypt.
    //return The encrypted data.


    // Concatenates two byte arrays.

    public static byte[] concat(byte[] a, byte[] b) {
        byte [] c;
        int i=0, j=0;
        c = new byte[a.length + b.length];
        for ( i = 0; i < a.length; i++) {
            c[i] = a[i];
        }
        for ( j = 0; j < b.length; j++) {
            c[a.length + j] = b[j];
        }
        return c;
    }



//======================================================================================================================

    // Converts a byte into an  BitSet.

    public static BitSet toBitSet(byte b) {
        BitSet bs;
        int i=0;
        bs = new BitSet(8);
        for ( i = 0; i < 8; i++) {
            if ((b & (1L << i)) > 0) {
                bs.set(7 - i);
            }
        }
        return bs;
    }

    // Converts a byte array to an extended BitSet.

    public static BitSet toBitSet(byte[] b) {
        int i=0,j=0;
        BitSet bs;
        bs = new BitSet(8 * b.length);
        for ( i = 0; i < b.length; i++) {
            for ( j = 0; j < 8; j++) {
                if ((b[i] & (1L << j)) > 0) {
                    bs.set(8 * i + (7 - j));
                }
            }
        }
        return bs;
    }


     // Converts an extended BitSet into a byte.

    public static byte toByte(BitSet b) {
        byte value = 0;
        int i=0;
        for ( i = 0; i < b.length(); i++) {
            if (b.get(i))
                value = (byte) (value | (1L << 7 - i));
        }
        return value;
    }


     //Converts a BitSet into a byte array.

    public static byte[] toByteArray(BitSet b) {
        int size ,i=0;

        byte [] value;
        size = (int) Math.ceil(b.length() / 8.0d);
        value = new byte[size];
        for ( i = 0; i < size; i++) {
            value[i] = toByte(b.get(i * 8, Math.min(b.length(), (i + 1) * 8)));
        }
        return value;
    }


     // Converts a hexadecimal String into a byte array (Big-Endian).

    public static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data;
        data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


     //Converts a byte array into a hexadecimal string (Big-Endian).

    public static String toHex(byte[] bytes) {
        BigInteger bi;
        bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

//======================================================================================================================



}
