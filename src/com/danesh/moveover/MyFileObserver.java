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
        sourcePath = source;
        destPath = dest;
    }

    @Override
    public void onEvent(int event, String path) {
        if ((FileObserver.CREATE & event)!=0) {
            File source = new File(sourcePath+path);
            File dest = new File(destPath+path);
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
