package com.danesh.moveover;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MoveOverActivity extends Activity implements OnCheckedChangeListener,OnClickListener,OnItemClickListener,OnItemLongClickListener {
    String sdcard = Environment.getExternalStorageDirectory().toString()+"/";
    String sourceFolder = "/mnt/sdcard/DCIM";
    String destFolder = "/mnt/sdcard/Test";
    EditText source,dest;
    ToggleButton service;
    ListView myList;
    Context mContext;
    Button add,chooseSource,chooseDest;
    static Map<String, ?> sharedMap;

    public static String getMap(String item) {
        return sharedMap.get(item).toString();
    }

    private void closeApp() {
        System.runFinalizersOnExit(true);
        System.exit(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print("------------------------------------------------");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mContext = getApplicationContext();
        final SharedPreferences myPrefs = mContext.getSharedPreferences("storedPreferences", MODE_PRIVATE);
        if (myPrefs.getBoolean("firstTime", true)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Welcome to moveOver !")
                   .setCancelable(false).setIcon(R.drawable.icon)
                   .setMessage(getResources().getString(R.string.firstTime))
                   .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           myPrefs.edit().putBoolean("firstTime", false).apply();
                       }
                   }).show();
        }
        source = (EditText)findViewById(R.id.source);
        dest = (EditText)findViewById(R.id.dest);
        source.setText(sourceFolder);
        dest.setText(destFolder);
        service = (ToggleButton)findViewById(R.id.toggleService);
        service.setChecked(LocalService.serviceRunning);
        service.setOnCheckedChangeListener(this);
        myList = (ListView)findViewById(R.id.listView1);
        myList.setOnItemClickListener(this);
        myList.setOnItemLongClickListener(this);
        add = (Button)findViewById(R.id.add);
        add.setOnClickListener(this);
        chooseSource = (Button)findViewById(R.id.chooseSource);
        chooseSource.setOnClickListener(this);
        chooseDest = (Button)findViewById(R.id.chooseDest);
        chooseDest.setOnClickListener(this);
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences myPrefs = mContext.getSharedPreferences("storedPreferences", MODE_PRIVATE);
        Boolean isChecked = myPrefs.getBoolean("startOnBoot", false);
        menu.add(Menu.NONE, 0, Menu.NONE, "Start onBoot").setIcon( isChecked ? android.R.drawable.button_onoff_indicator_on :
            android.R.drawable.button_onoff_indicator_off).setCheckable(true).setChecked(isChecked);
        menu.add(Menu.NONE, 1, Menu.NONE, "Exit");
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
        case 0:
            Boolean isChecked = !item.isChecked();
            SharedPreferences myPrefs = mContext.getSharedPreferences("storedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.putBoolean("startOnBoot", isChecked);
            prefsEditor.apply();
            item.setChecked(isChecked);
            item.setIcon( isChecked ? android.R.drawable.button_onoff_indicator_on : android.R.drawable.button_onoff_indicator_off);
            break;
        case 1:
            closeApp();
            break;
        }
        return true;
    }

    private void setAdapter() {
        SharedPreferences myPrefs = mContext.getSharedPreferences("storedArray", MODE_PRIVATE);
        sharedMap = myPrefs.getAll();
        myList.setAdapter(new IconicAdapter());
    }

    class IconicAdapter extends ArrayAdapter<Object> {
        IconicAdapter(){
            super(MoveOverActivity.this, android.R.layout.simple_list_item_1, sharedMap.keySet().toArray());
        }

        public View getView(int pos, View convertView, ViewGroup parent){
            LayoutInflater inf = getLayoutInflater();
            if(convertView==null){
                convertView = inf.inflate(R.layout.row,null);
            }
            TextView label = (TextView) convertView.findViewById(R.id.source);
            label.setText(sharedMap.keySet().toArray()[pos].toString());
            label = (TextView) convertView.findViewById(R.id.destination);
            label.setText(sharedMap.values().toArray()[pos].toString());
            return convertView;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (sharedMap.isEmpty()){
            showToast("Nothing is being tracked");
            service.setChecked(!isChecked);
            return;
        }
        Intent mine = new Intent(this, LocalService.class);
        if (isChecked){
            startService(mine);
        }else{
            stopService(mine);    
        }
    }

    public static void print(String msg){
        System.out.println("Danny "+msg);
    }

    public void showToast(String msg) {
        Toast pop = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        pop.show();
    }

    private boolean appInstalledOrNot(Intent intent)
    {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onClick(View arg0) {
        if (arg0==chooseSource || arg0 == chooseDest){
            Intent intent = new Intent("org.openintents.action.PICK_DIRECTORY");
            if (appInstalledOrNot(intent)){
                startActivityForResult(intent, arg0 == chooseSource ? 0 : 1);
            }else{
                showToast("OI file manager not found");
                intent = new Intent( Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:org.openintents.filemanager"));
                startActivity(intent);
            }
        }else if (arg0 == add){
            String sourceText = source.getText().toString();
            String destText = dest.getText().toString();
            File testsource = new File(sourceText);
            File testdest = new File(destText);
            if (sourceText.charAt(sourceText.length()-1)!='/'){
                source.setText(sourceText+"/");
            }
            if (destText.charAt(destText.length()-1)!='/'){
                dest.setText(destText+"/");
            }
            if (!testsource.isDirectory()){
                showToast("Source directory invalid or is not a directory");
                return;
            }
            if (!testdest.isDirectory()){
                showToast("Destination directory invalid or is not a directory");
                return;
            }
            if (!sourceText.contains("/sdcard/") || !destText.contains("/sdcard/")){
                ShellCommand root = new ShellCommand();
                if (!root.canSU()){
                    showToast("Root was not found");
                    return;
                }
            }
            if (checkIfSourceIsTarget(sourceText)){
                showToast("This source was already saved as a target");
                return;
            }
            if (sourceText.equals(destText)){
                showToast("Source cannot be same as target");
                return;
            }
            if (checkIfMappingAlreadyExists(sourceText,destText)){
                showToast("This mapping already exists");
                return;
            }
            modifyPreference(1,"");
        }
    }

    public boolean checkIfSourceIsTarget(String source){
        return sharedMap.values().contains(source);
    }

    public boolean checkIfMappingAlreadyExists(String source, String target){
        return sharedMap.keySet().contains(source) && sharedMap.get(source).equals(target);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null){
            if (requestCode == 0){
                source.setText(data.getData().getPath()+"/");
            }else{
                dest.setText(data.getData().getPath()+"/");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        TextView sourceView = (TextView) arg1.findViewById(R.id.source);
        TextView destView = (TextView) arg1.findViewById(R.id.destination);
        String sourceText = sourceView.getText().toString();
        String destText = destView.getText().toString();
        source.setText(sourceText);
        dest.setText(destText);
    }

    public void modifyPreference(int mode, String preference){
        SharedPreferences myPrefs = mContext.getSharedPreferences("storedArray", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        if (mode == 1){
            prefsEditor.putString(source.getText().toString(), dest.getText().toString());
            dest.setText("");
            source.setText("");
        }else{
            prefsEditor.remove(preference);
        }
        prefsEditor.apply();
        setAdapter();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, final View arg1, final int arg2, long arg3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you delete?")
        .setCancelable(false)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                TextView sourceView = (TextView) arg1.findViewById(R.id.source);
                String sourceText = sourceView.getText().toString();
                modifyPreference(0,sourceText);
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
        return false;
    }
}