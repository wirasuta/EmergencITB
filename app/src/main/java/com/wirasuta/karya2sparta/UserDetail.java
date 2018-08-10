package com.wirasuta.karya2sparta;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wirasuta.karya2sparta.R;

public class UserDetail extends AppCompatActivity {

    SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);

        String name = myPrefs.getString("nameKey","No name");
        int nim = myPrefs.getInt("NIMKey",0);
        int telp = myPrefs.getInt("telpKey",0);
        int telpd = myPrefs.getInt("telpdKey",0);
        String alamat = myPrefs.getString("alamatKey","No name");
        String alamatd = myPrefs.getString("alamatdKey","No name");

        TextView label = findViewById(R.id.labelID);
        label.setText(name + ", " + nim);

        TextView label2 = findViewById(R.id.lanjutID);
        label2.setText("No. Telp: " + telp + ", Darurat: " + telpd + ", Alamat: " + alamat + ", Darurat: " + alamatd);
    }

    public void onButtonClick(View v){

        //get reference to TextView
        TextView label = (TextView) findViewById(R.id.labelID);

        //get references to Name and Age EditTexts
        EditText nameEditText = (EditText) findViewById(R.id.nameID);
        EditText NIMEditText = (EditText) findViewById(R.id.NIMID);
        EditText phoneEditText = (EditText) findViewById(R.id.TelpID); //phone = no.telpon
        EditText dphoneEditText = (EditText) findViewById(R.id.TelpDarID); //dphone = no. darurat
        EditText addrEditText = (EditText) findViewById(R.id.AlamatID); //Alamat=addr
        EditText daddrEditText = (EditText) findViewById(R.id.AlamatDarID); //Alamat Darurat=daddr


        //set up SharedPreferences
        myPrefs = getSharedPreferences("prefID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("nameKey", nameEditText.getText().toString());
        editor.putInt("NIMKey", Integer.parseInt(NIMEditText.getText().toString()));
        editor.putInt("telpKey", Integer.parseInt(phoneEditText.getText().toString()));
        editor.putInt("telpdKey", Integer.parseInt(dphoneEditText.getText().toString()));
        editor.putString("alamatKey", addrEditText.getText().toString());
        editor.putString("alamatdKey", daddrEditText.getText().toString());
        editor.apply();
        label.setText("Saved");
    }
}
