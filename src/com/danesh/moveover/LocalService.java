package com.danesh.moveover;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocalService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

    }
    @Override    
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        onStartCommand(intent,0,startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String source = intent.getStringExtra("source");
        String dest = intent.getStringExtra("dest");
        File sourceDir = new File(source);
        if (sourceDir.isDirectory()){
            for (File file : sourceDir.listFiles()){
                if (file.isDirectory()){
                    MyFileObserver newOne = new MyFileObserver(file.getPath().toString()+"/",dest+file.getName().toString()+"/");
                    newOne.startWatching();
                }
            }
        }
        return 0;
    }
}