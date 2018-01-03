// IScanCallback.aidl
package com.demo.scansd.aidl;

import java.util.List;
import com.demo.scansd.aidl.BiggestFile;
import com.demo.scansd.aidl.Extension;

// Declare any non-default types here with import statements
interface IScanCallback {
    oneway void errorMessage(String msg);
    oneway void updateBiggestFiles(in List<BiggestFile> mList);
    oneway void updateAverage(long fileCount,long fileLength);
    oneway void updateExtensions(in List<Extension> mList);
    oneway void scanFinished();
}
