package com.ang.androidmutualpython;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.srplab.www.starcore.StarCoreFactory;
import com.srplab.www.starcore.StarCoreFactoryPath;
import com.srplab.www.starcore.StarObjectClass;
import com.srplab.www.starcore.StarServiceClass;
import com.srplab.www.starcore.StarSrvGroupClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.Host = this;
    }

    public StarSrvGroupClass srvGroup;
    public static MainActivity Host;
    public void click(View view) {
        File destDir = new File("/data/data/" + getPackageName() + "/files");
        if (!destDir.exists())
            destDir.mkdirs();
        java.io.File python2_7_libFile = new java.io.File("/data/data/" + getPackageName() + "/files/python3.9.zip");
        if (!python2_7_libFile.exists()) {
            try {
                copyFile(this, "python3.9.zip", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            copyFile(this, "_struct.cpython-39.so", null);
            copyFile(this, "binascii.cpython-39.so", null);
            copyFile(this, "zlib.cpython-39.so", null);
            copyFile(this, "test_calljava.py", null);
        } catch (Exception e) {
            System.out.println(e);
        }

        //----a test file to be read using python, we copy it to files directory
//        try {
//            copyFile(this, "test.txt", "");
//            copyFile(this, "test_calljava.py", "");
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        /*----load test.py----*/
        String pystring = null;
        try {
            AssetManager assetManager = getAssets();
            InputStream dataSource = assetManager.open("MathTest.py");
            int size = dataSource.available();
            byte[] buffer = new byte[size];
            dataSource.read(buffer);
            dataSource.close();
            pystring = new String(buffer);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            //--load python34 core library first;
            System.load(this.getApplicationInfo().nativeLibraryDir + "/libpython3.9.so");
        } catch (UnsatisfiedLinkError ex) {
            System.out.println(ex.toString());
        }

        /*----init starcore----*/
        StarCoreFactoryPath.StarCoreCoreLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreShareLibraryPath = this.getApplicationInfo().nativeLibraryDir;
        StarCoreFactoryPath.StarCoreOperationPath = "/data/data/" + getPackageName() + "/files";

        StarCoreFactory starcore = StarCoreFactory.GetFactory();
        Integer s = new Random().nextInt(100);
        StarServiceClass service = starcore._InitSimple("MathTest" + s, "123", 0, 0);
        srvGroup = (StarSrvGroupClass) service._Get("_ServiceGroup");
        service._CheckPassword(false);

        /*----run python code----*/
        srvGroup._InitRaw("python39", service);
        StarObjectClass python = service._ImportRawContext("python", "", false, "");
        python._Call("import", "sys");

        StarObjectClass pythonSys = python._GetObject("sys");
        StarObjectClass pythonPath = (StarObjectClass) pythonSys._Get("path");
        pythonPath._Call("insert", 0, "/data/data/" + getPackageName() + "/files/python3.9.zip");
        pythonPath._Call("insert", 0, this.getApplicationInfo().nativeLibraryDir);
        pythonPath._Call("insert", 0, "/data/data/" + getPackageName() + "/files");

        python._Call("execute", pystring);
        Object object = python._Call("add", 11, 22);
        Toast.makeText(this,object.toString(),Toast.LENGTH_LONG).show();

        String CorePath = "/data/data/" + getPackageName() + "/files";
        python._Set("JavaClass", CallBackClass.class);
        service._DoFile("python", CorePath + "/test_calljava.py", "");
    }

    private void copyFile(Activity c, String Name, String desPath) throws IOException {
        File outfile = null;
        if (desPath != null)
            outfile = new File("/data/data/" + getPackageName() + "/files/" + desPath + Name);
        else
            outfile = new File("/data/data/" + getPackageName() + "/files/" + Name);
        //if (!outfile.exists()) {
        outfile.createNewFile();
        FileOutputStream out = new FileOutputStream(outfile);
        byte[] buffer = new byte[1024];
        InputStream in;
        int readLen = 0;
        if (desPath != null)
            in = c.getAssets().open(desPath + Name);
        else
            in = c.getAssets().open(Name);
        while ((readLen = in.read(buffer)) != -1) {
            out.write(buffer, 0, readLen);
        }
        out.flush();
        in.close();
        out.close();
        //}
    }
}