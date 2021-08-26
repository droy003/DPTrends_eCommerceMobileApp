package com.company.dptrends.Prevalent;

import android.widget.Toast;

import com.company.dptrends.LoginActivity;
import com.company.dptrends.Model.Users;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class Prevalent {

    public static Users currentOnlineUser;

    public static final String UserPhoneKey = "UserPhone";
    public static final String UserPasswordKey = "UserPassword";
    public static final String EncryptPassword = "DPTrends";



    public static final String ProductIDKey = "pid";

    public static final int MaxItems = 10;
    public static String[] getStringArray(int maxitems){

        if (maxitems == 0){
            String[] items = new String[0];
            return items;
        }
        else {
            int minValue = Math.min(maxitems, MaxItems);
            String[] items = new String[minValue];
            for (int i = 1; i <= minValue; i++) {
                items[i-1]=Integer.toString(i);
            }
            return items;
        }

    }

}
