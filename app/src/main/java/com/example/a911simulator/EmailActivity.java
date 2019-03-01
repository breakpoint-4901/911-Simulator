package com.example.a911simulator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class EmailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        //grab necessary ui elements
        final EditText emailEditText = findViewById(R.id.emailEditText);
        Button submitEmailButton = findViewById(R.id.submitEmailButton);

        //handle email submission logic
        submitEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //grab email from editText
                String email = emailEditText.getText().toString().trim();

                //validate email
                if(isValidEmail(email)){
                    //create email intent
                    

                }
                else{
                    //create alert dialog
                    DialogFragment alert = new InvalidEmailDialogFragment();

                    //show alert
                    alert.show(getSupportFragmentManager(), "sum");
                }


            }
        });
    }

    //special thanks to StackOverflow users user1737884 and Andrey on
    //https://stackoverflow.com/questions/12947620/email-address-validation-in-android-on-edittext
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
