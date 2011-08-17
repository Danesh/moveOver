package com.danesh.moveover;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.FileObserver;

public class MyFileObserver extends FileObserver{
    String destPath,sourcePath;
    public MyFileObserver(String source, String dest) {
        super(source);
        MoveOverActivity.print(source + " " + dest);
        sourcePath = source;
        destPath = dest;
    }

    @Override
    public void onEvent(int event, String path) {
        debugInfo(event,path);
        if ((FileObserver.CREATE & event)!=0) {
            File source = new File(sourcePath+path);
            File dest = new File(destPath+path);
            MoveOverActivity.print("Copy : " + source.toString() + " to : " + dest.toString());
            try {
                copyDirectory(source,dest);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if (source.isDirectory()){
                LocalService.cycleThrough(source, dest.toString());
            }
            return;
        }else if ((FileObserver.MODIFY & event)!=0) {
            File source = new File(sourcePath+path);
            File dest = new File(destPath+path);
            try {
                copyDirectory(source,dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
    }

    private void debugInfo(int e, String path) {
        String msg="";
        if ((e&ACCESS)!=0){
            msg = "ACCESS";
        }else if ((e&ATTRIB)!=0){
            msg = "ATTRIB";
        }else if ((e&CLOSE_NOWRITE)!=0){
            msg = "CLOSE_NOWRITE";
        }else if ((e&CLOSE_WRITE)!=0){
            msg = "CLOSE_WRITE";
        }else if ((e&CREATE)!=0){
            msg = "CREATE";
        }else if ((e&DELETE)!=0){
            msg = "DELETE";
        }else if ((e&DELETE_SELF)!=0){
            msg = "DELETE_SELF";
        }else if ((e&MODIFY)!=0){
            msg = "MODIFY";
        }else if ((e&MOVED_FROM)!=0){
            msg = "MOVED_FROM";
        }else if ((e&MOVED_TO)!=0){
            msg = "MOVED_TO";
        }else if ((e&MOVE_SELF)!=0){
            msg = "MOVE_SELF";
        }else if ((e&OPEN)!=0){
            msg = "OPEN";
        }
        MoveOverActivity.print("Mode : "+msg + " " + path);
    }

    public void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
        File path = new File(targetLocation.getParent());
        if (!path.exists()){
            path.mkdirs();
        }
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
}
