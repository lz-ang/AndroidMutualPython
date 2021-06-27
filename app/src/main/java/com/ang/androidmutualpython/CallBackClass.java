package com.ang.androidmutualpython;

import android.util.Log;

import com.srplab.www.starcore.StarObjectClass;
import com.srplab.www.starcore.StarParaPkgClass;

public class CallBackClass {
    private static final String TAG = "CallBackClass";
    StarObjectClass pythonClass;

    public CallBackClass(String info) {
        Log.e(TAG , info);
    }

    public void callback(float val) {
        Log.e(TAG , "" + val);
    }

    public void callback(String val) {
        Log.e(TAG , val);
    }

    public void SetPythonObject(Object rb) {
        pythonClass = (StarObjectClass) rb;
        String aa = "";
        StarParaPkgClass data1 = MainActivity.Host.srvGroup._NewParaPkg("b", 789, "c", 456, "a", 123)._AsDict(true);
        Object d1 = pythonClass._Call("dumps", data1, MainActivity.Host.srvGroup._NewParaPkg("sort_keys", true)._AsDict(true));
        Log.e(TAG , d1+"");
        Object d2 = pythonClass._Call("dumps", data1, null);
        Log.e(TAG , d2+"");
        Object d3 = pythonClass._Call("dumps", data1, MainActivity.Host.srvGroup._NewParaPkg("sort_keys", true, "indent", 4)._AsDict(true));
        Log.e(TAG , d3+"");
    }
}
