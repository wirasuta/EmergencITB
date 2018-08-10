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

    EditText NIMEditText;
    EditText phoneEditText; //phone = no.telpon
    EditText dphoneEditText; //dphone = no. darurat
    EditText addrEditText; //Alamat=addr
    EditText daddrEditText; //Alamat Darurat=daddr
    Switch enableSw;
    EditText nameEditText;

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
        daddrEditText = (EditText) findViewById(R.id.AlamatDarID);
        enableSw = (Switch) findViewById(R.id.enableSw);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);

        if (!myPrefs.getBoolean("firstLaunch",true)){
            nameEditText.setText(myPrefs.getString("nameKey","No name"));
            NIMEditText.setText(myPrefs.getString("NIMKey","12317000"));
            phoneEditText.setText(myPrefs.getString("telpKey","02212345678"));
            dphoneEditText.setText(myPrefs.getString("telpdKey","02212345678"));
            addrEditText.setText(myPrefs.getString("alamatKey","No name"));
            daddrEditText.setText(myPrefs.getString("alamatdKey","No name"));
        }

        enableSw.setChecked(myPrefs.getBoolean("enableGeofence",true));
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
        editor.putString("alamatdKey", daddrEditText.getText().toString());
        editor.putBoolean("enableGeofence", enableSw.isChecked());
        if (myPrefs.getBoolean("firstLaunch",true)) editor.putBoolean("firstLaunch",false);
        editor.apply();
        Toast.makeText(this,"User Data Saved",Toast.LENGTH_SHORT).show();

        Intent backToMain = new Intent(this,MainActivity.class);
        startActivity(backToMain);
    }
}
