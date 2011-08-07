package com.danesh.moveover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MoveOverActivity extends Activity implements OnCheckedChangeListener,OnClickListener {
    String sdcard = Environment.getExternalStorageDirectory().toString()+"/";
    String sourceFolder = sdcard+"DCIM"+"/";
    String destFolder = sdcard+"synca"+"/";
    TextView source,dest;
    ToggleButton service;
    ListView myList;
    Button add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print("------------------------------------------------");
        setContentView(R.layout.main);
        source = (TextView)findViewById(R.id.source);
        dest = (TextView)findViewById(R.id.dest);
        source.setText(sourceFolder);
        dest.setText(destFolder);
        service = (ToggleButton)findViewById(R.id.toggleButton1);
        service.setOnCheckedChangeListener(this);
        myList = (ListView)findViewById(R.id.listView1);
        add = (Button)findViewById(R.id.button1);
        add.setOnClickListener(this);
        setAdapter();
    }

    private void setAdapter() {
        ArrayList<String> results = new ArrayList<String>();
        SharedPreferences myPrefs = this.getSharedPreferences("storedArray", MODE_PRIVATE);
        for (String item : myPrefs.getAll().keySet()){
            results.add(item + " -> " + myPrefs.getAll().get(item));
        }
        myList.setAdapter(new ArrayAdapter<String>(this, R.layout.row, R.id.label, results));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent mine = new Intent(this, LocalService.class);
        if (isChecked){
            mine.putExtra("source", source.getText().toString());
            mine.putExtra("dest", dest.getText().toString());
            startService(mine);
        }else{
            stopService(mine);    
        }
    }
    
    public static void print(String msg){
        System.out.println("Danny "+msg);
    }

    @Override
    public void onClick(View arg0) {
        SharedPreferences myPrefs = this.getSharedPreferences("storedArray", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString(source.getText().toString(), dest.getText().toString());
        prefsEditor.commit();
        setAdapter();
    }
}