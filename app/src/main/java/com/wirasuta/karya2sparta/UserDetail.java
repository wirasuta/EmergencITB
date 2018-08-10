package com.wirasuta.karya2sparta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wirasuta.karya2sparta.R;

public class UserDetail extends AppCompatActivity {

    SharedPreferences myPrefs;

    EditText nameEditText;
    EditText NIMEditText;
    EditText phoneEditText; //phone = no.telpon
    EditText dphoneEditText; //dphone = no. darurat
    EditText addrEditText; //Alamat=addr
    EditText fakultasEditText;
    EditText emailEditText;
    Switch enableSw;
    Switch enableSw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        //get references to Name and Age EditTexts
        nameEditText = (EditText) findViewById(R.id.nameID);
        NIMEditText = (EditText) findViewById(R.id.NIMID);
        phoneEditText = (EditText) findViewById(R.id.TelpID);
        dphoneEditText = (EditText) findViewById(R.id.TelpDarID);
        addrEditText = (EditText) findViewById(R.id.AlamatID);
        fakultasEditText = (EditText) findViewById(R.id.fakultasID);
        emailEditText = findViewById(R.id.emailID);
        enableSw = (Switch) findViewById(R.id.enableSw);
        enableSw2 = (Switch) findViewById(R.id.enableSw2);
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);

        if (!myPrefs.getBoolean("firstLaunch",true)){
            nameEditText.setText(myPrefs.getString("nameKey","No name"));
            NIMEditText.setText(myPrefs.getString("NIMKey","12317000"));
            phoneEditText.setText(myPrefs.getString("telpKey","02212345678"));
            dphoneEditText.setText(myPrefs.getString("telpdKey","02212345678"));
            addrEditText.setText(myPrefs.getString("alamatKey","No Address"));
            fakultasEditText.setText(myPrefs.getString("fakultasKey","Unknown"));
            emailEditText.setText(myPrefs.getString("emailKey","Unknown"));
        }

        enableSw.setChecked(myPrefs.getBoolean("enableGeofence",true));
        enableSw2.setChecked(myPrefs.getBoolean("contactK3L",false));
    }

    public void onButtonClick(View v){

        //set up SharedPreferences
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("nameKey", nameEditText.getText().toString());
        editor.putString("NIMKey", NIMEditText.getText().toString());
        editor.putString("telpKey", phoneEditText.getText().toString());
        editor.putString("telpdKey", dphoneEditText.getText().toString());
        editor.putString("alamatKey", addrEditText.getText().toString());
        editor.putString("fakultasKey", fakultasEditText.getText().toString());
        editor.putString("emailKey",emailEditText.getText().toString());
        editor.putBoolean("enableGeofence", enableSw.isChecked());
        editor.putBoolean("contactK3L", enableSw2.isChecked());
        if (myPrefs.getBoolean("firstLaunch",true)) editor.putBoolean("firstLaunch",false);
        editor.apply();
        Toast.makeText(this,"User Data Saved",Toast.LENGTH_SHORT).show();

        Intent backToMain = new Intent(this,MainActivity.class);
        startActivity(backToMain);
    }
}
