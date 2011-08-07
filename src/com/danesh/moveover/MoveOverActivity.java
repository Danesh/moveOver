package com.danesh.moveover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MoveOverActivity extends Activity implements OnCheckedChangeListener {
    String sdcard = Environment.getExternalStorageDirectory().toString()+"/";
    String sourceFolder = sdcard+"DCIM"+"/";
    String destFolder = sdcard+"synca"+"/";
    TextView source,dest;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print("------------------------------------------------");
        setContentView(R.layout.main);
        source = (TextView)findViewById(R.id.source);
        dest = (TextView)findViewById(R.id.dest);
        source.setText(sourceFolder);
        dest.setText(destFolder);
        ToggleButton service = (ToggleButton)findViewById(R.id.toggleButton1);
        service.setOnCheckedChangeListener(this);
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
}