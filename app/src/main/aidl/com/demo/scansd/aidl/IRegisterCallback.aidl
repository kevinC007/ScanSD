// IRegisterCallback.aidl
package com.demo.scansd.aidl;

// Declare any non-default types here with import statements
import com.demo.scansd.aidl.IScanCallback;

interface IRegisterCallback {
    oneway void registerCallback(IScanCallback  callback);
    oneway void unregisterCallback(IScanCallback callback);
    oneway void start();
    oneway void changeStatus(int status);
}
