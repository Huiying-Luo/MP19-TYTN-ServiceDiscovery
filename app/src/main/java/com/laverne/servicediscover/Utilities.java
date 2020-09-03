package com.laverne.servicediscover;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Utilities {

    public static void showAlertDialogwithOkButton(Context context, String title, String message) {
        // create a alert dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create().show();
    }
}
