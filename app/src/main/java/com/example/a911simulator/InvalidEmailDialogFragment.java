package com.example.a911simulator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

//can be used for invalid email alerts
public class InvalidEmailDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //using builder class for convenience
        AlertDialog.Builder invalidDialog = new AlertDialog.Builder(getActivity(), R.style.EmailDialogTheme);
        invalidDialog.setTitle("Invalid email");
        invalidDialog.setMessage("Sorry, this is an invalid email. Please verify it is correct.");
        invalidDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Ok", "Ok");
            }
        });

        return invalidDialog.create();
    }
}
