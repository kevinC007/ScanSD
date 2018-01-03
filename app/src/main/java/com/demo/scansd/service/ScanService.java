package com.demo.scansd.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.demo.scansd.Constant;
import com.demo.scansd.aidl.BiggestFile;
import com.demo.scansd.aidl.Extension;
import com.demo.scansd.aidl.IRegisterCallback;
import com.demo.scansd.aidl.IScanCallback;
import com.demo.scansd.util.BiggestFileCompartor;
import com.demo.scansd.util.ExtensionCompartor;
import com.demo.scansd.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScanService extends Service {
    public ScanService() {
    }

    private RemoteCallbackList<IScanCallback> callbackList = new RemoteCallbackList<IScanCallback>();

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }
    private int status = Constant.STATUS_STOP;
    private List<BiggestFile> biggestFiles = null;
    private long scanedFilesLength = 0;
    private long scanedFilesCount = 0;
    private Map<String, Integer> extensionsMap = null;
    private List<Extension> extensionList = null;
    List<Map.Entry<String, Integer>> allExtensionList = null;
//    private int scanedFile = 0;
    private BiggestFileCompartor compartor1 = new BiggestFileCompartor();
    private ExtensionCompartor compartor2 = new ExtensionCompartor();

    private IBinder serviceBinder = new IRegisterCallback.Stub() {
//        public boolean isRunning;
        @Override
        public void registerCallback(IScanCallback  callback) throws RemoteException {
            callbackList.register(callback);
            initData();
//            isRunning = true;
        }

        @Override
        public void unregisterCallback(IScanCallback callback) throws RemoteException {
            callbackList.unregister(callback);
            initData();
//            isRunning = false;
        }

        @Override
        public void changeStatus(int s){
            status = s;
        }

        public void initData(){
            if(biggestFiles == null){
                biggestFiles = new ArrayList<BiggestFile>();
            }else{
                biggestFiles.clear();
            }
            if(extensionsMap == null){
                extensionsMap = new HashMap<String, Integer>();
            }else{
                extensionsMap.clear();
            }
            if(extensionList == null){
                extensionList = new ArrayList<Extension>();
            }else{
                extensionList.clear();
            }
            scanedFilesLength = 0;
            scanedFilesCount = 0;
        }



        @Override
        public void start() throws RemoteException {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (status == Constant.STATUS_START){
                        File sdRoot = Environment.getExternalStorageDirectory();
                        if(!sdRoot.exists()){
//                int num = callbackList.beginBroadcast();
//                            try {
//                                callbackList.getBroadcastItem(0).errorMessage("No Sdcard!");
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }

                        }
                        ergodicFile(sdRoot);
                        if(scanedFilesCount > 0)
                            notifyUI();

                        status = Constant.STATUS_STOP;
                        int num = callbackList.beginBroadcast();
                        try {
                            if(num > 0)
                                callbackList.getBroadcastItem(0).scanFinished();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        callbackList.finishBroadcast();
                    }
                }
            }).start();



//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while(isRunning) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        count ++;
//                        int num = callbackList.beginBroadcast();
//                        for (int i = 0; i < num; i++) {
//                            try {
//                                System.out.println("nnnuuummm === " + num );
//                                callbackList.getBroadcastItem(i).updateData(count);
//                            } catch (RemoteException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        callbackList.finishBroadcast();
//                    }
//                }
//            }).start();
        }
    };

    private void ergodicFile(File file){
//        try {
//            Thread.sleep(50);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(status == Constant.STATUS_STOP)
            return;
        while(status == Constant.STATUS_PAUSE){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(file.isFile()){
            scanedFilesLength += file.length();
            scanedFilesCount ++;
//            scanedFile++;
            if(biggestFiles.size() <10){
                biggestFiles.add(new BiggestFile(file.getAbsolutePath(), file.length() ));
                Collections.sort(biggestFiles, compartor1);
            }else{
                BiggestFile temp = biggestFiles.get(9);
                if(temp.getFileLength() < file.length()){
                    biggestFiles.remove(9);
                    biggestFiles.add(9, new BiggestFile(file.getAbsolutePath(), file.length()));
                    Collections.sort(biggestFiles, compartor1);
                }
            }
            String extension = FileUtils.getExtensionName(file.getName());
            if(extensionsMap.containsKey(extension)){
                extensionsMap.put(extension, extensionsMap.get(extension).intValue() + 1);
            }else{
                extensionsMap.put(extension,  1);
            }


            if(scanedFilesCount %Constant.SCAN_STEP == 0){
                //save to db and update ui
                notifyUI();
            }
        }else{
            File[] files = file.listFiles();
            if(files != null) {
                for (File f : files) {
                    ergodicFile(f);
                }
            }
        }
    }

    private void notifyUI(){

//        for(int i=0;i<biggestFiles.size();i++){
//            File f = biggestFiles.get(i);
//            System.out.println(i + " == " + f.getAbsolutePath() + " == " + f.length());
//        }
////        System.out.println("scanedFile = " + scanedFile);
//        System.out.println("scanedFilesCount = " + scanedFilesCount);
//        System.out.println("scanedFilesLength = " + scanedFilesLength);
//
//        Iterator iter = extensionsMap.entrySet().iterator();
//        while (iter.hasNext()) {
//            Map.Entry entry = (Map.Entry) iter.next();
//            String key = entry.getKey().toString();
//            Integer val = Integer.parseInt(entry.getValue().toString());
//            if(val.intValue() >1)
//            System.out.println(key + " count = " + val.intValue());
//        }

        int num = callbackList.beginBroadcast();
        if(num == 0)
            return;
        try {
//            callbackList.getBroadcastItem(0).errorMessage("No Sdcard!");
            callbackList.getBroadcastItem(0).updateAverage(scanedFilesCount, scanedFilesLength);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            callbackList.getBroadcastItem(0).updateBiggestFiles(biggestFiles);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(allExtensionList == null){
            allExtensionList = new ArrayList<Map.Entry<String, Integer>>(
                    extensionsMap.entrySet());
        }else{
            allExtensionList.clear();
            allExtensionList = new ArrayList<Map.Entry<String, Integer>>(
                    extensionsMap.entrySet());
        }
        Collections.sort(allExtensionList, compartor2);
        extensionList.clear();
        for(int i=0;i<5;i++){
            Map.Entry<String, Integer> extensionEntry = (Map.Entry<String, Integer>)allExtensionList.get(i);
            Extension extension = new Extension();
            extension.setExtensionName(extensionEntry.getKey());
            extension.setCount(extensionEntry.getValue());
            extensionList.add(extension);
        }
        try {
            callbackList.getBroadcastItem(0).updateExtensions(extensionList);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        callbackList.finishBroadcast();
    }


}
