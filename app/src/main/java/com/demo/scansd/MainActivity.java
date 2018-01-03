package com.demo.scansd;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.demo.material.widget.Button;
import com.demo.material.widget.ProgressView;
import com.demo.scansd.aidl.BiggestFile;
import com.demo.scansd.aidl.Extension;
import com.demo.scansd.aidl.IRegisterCallback;
import com.demo.scansd.aidl.IScanCallback;
import com.demo.scansd.fragment.AverageFragment;
import com.demo.scansd.fragment.BiggestFilesFragment;
import com.demo.scansd.fragment.ExtensionsFragment;
import com.demo.scansd.service.ScanService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Handler.Callback{



    private BiggestFilesFragment biggestFilesFragment = null;
    private AverageFragment averageFragment = null;
    private ExtensionsFragment extensionsFragment = null;

    private static int STATUS = Constant.STATUS_STOP;

    private Intent intent = null;
    private boolean isAIDLServiceConnected = false;
    IRegisterCallback registerCallback;

    private ProgressView mProgressView;
    private Button mButton;
    private ViewPager mViewPager;
    private FloatingActionButton floatingActionButton;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressView = (ProgressView)findViewById(R.id.progress);
        mButton = (Button)findViewById(R.id.button_raise);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.shareBtn);

        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Biggest Files"));
        tabLayout.addTab(tabLayout.newTab().setText("Avg Size"));
        tabLayout.addTab(tabLayout.newTab().setText("Extensions"));
        tabLayout.setupWithViewPager(mViewPager);

        mHandler = new Handler(this);

        intent = new Intent(this, ScanService.class);

        initNotification();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (STATUS){
                    case Constant.STATUS_START:
                        stopBtnClicked();
//                        stopAll();
//                        mButton.setText("Start");
//                        STATUS = Constant.STATUS_STOP;
                        break;
                    case Constant.STATUS_STOP:
                        mHandler.sendEmptyMessageDelayed(Constant.MSG_START_PROGRESS, 0);
                        startAIDLMethod();
                        mButton.setText("Stop");
                        STATUS = Constant.STATUS_START;
                        break;
                    case Constant.STATUS_PAUSE:
                        try {
                            registerCallback.changeStatus(Constant.STATUS_START);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(Constant.MSG_START_PROGRESS);
                        mButton.setText("Stop");
                        STATUS = Constant.STATUS_START;
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void share(){
//        biggestFilesFragment

        String bfContent = "Biggest Files: \n";
        String eContent = "Extension: \n";
        List<BiggestFile> bfList = null;
        String averageContent = "";
        List<Extension> eList = null;
        if(biggestFilesFragment != null) {
            bfList = biggestFilesFragment.getBittestFile();
            if (bfList != null) {
                for (BiggestFile bf : bfList) {
                    bfContent += "File Path: " + bf.getFilePath() + '\n';
                    bfContent += "File length is " + bf.getFileLength() + " bytes.\n";
                }
            }
        }
        if(averageFragment != null){
            averageContent = averageFragment.getAverageText() + "\n";
        }
        if(extensionsFragment != null) {
            eList = extensionsFragment.getExtensionList();
            if (eList != null) {
                for (Extension extension : eList) {
                    eContent += "Extension name: " + extension.getExtensionName() + "\n";
                    eContent += "Extension count: " + extension.getCount() + "\n";
                }
            }
        }

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        share.putExtra(Intent.EXTRA_TEXT, bfContent + "\n" + averageContent + "\n" + eContent);

        startActivity(Intent.createChooser(share, "Share link!"));
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    android:configChanges="orientation|keyboardHidden|screenSize"
//    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        STATUS = savedInstanceState.getInt("status");
        switch (STATUS){
            case Constant.STATUS_START:
                mButton.setText("Stop");
                mHandler.sendEmptyMessage(Constant.MSG_START_PROGRESS);
                break;
            case Constant.STATUS_STOP:
                mButton.setText("Start");
                break;
            case Constant.STATUS_PAUSE:
                mButton.setText("Resume");
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("status", STATUS);
    }

    private void stopBtnClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to stop scanning?")
                .setPositiveButton("STOP COMPLETELY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopAll();
                        mButton.setText("Start");
                        STATUS = Constant.STATUS_STOP;
                    }
                })
                .setNegativeButton("PAUSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            registerCallback.changeStatus(Constant.STATUS_PAUSE);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        mButton.setText("Resume");
                        mHandler.sendEmptyMessage(Constant.MSG_STOP_PROGRESS);
                        STATUS = Constant.STATUS_PAUSE;
                    }
                })
                .setNeutralButton("CANCEL", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        stopAll();
        super.onDestroy();
    }

    private void stopAll() {
        if (isAIDLServiceConnected) {
            try {
                registerCallback.unregisterCallback(scanCallback);
                registerCallback.changeStatus(Constant.STATUS_STOP);
                mHandler.sendEmptyMessage(Constant.MSG_STOP_PROGRESS);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(AIDLServiceConnection);
            isAIDLServiceConnected = false;
        }
    }

    private void startAIDLMethod() {
        bindService(intent, AIDLServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection AIDLServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isAIDLServiceConnected = true;
            registerCallback = IRegisterCallback.Stub.asInterface(service);
            try {
                registerCallback.registerCallback(scanCallback);
                registerCallback.changeStatus(Constant.STATUS_START);
                registerCallback.start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IScanCallback scanCallback = new IScanCallback.Stub() {

//        @Override
//        public void updateData(int num) throws RemoteException {
////            mCount = num;
////            updateCount();
//            System.out.println("num = " + num);
//        }

        @Override
        public void errorMessage(String msg)throws  RemoteException{

        }

        @Override
        public void updateAverage(long fileCount, long fileLength) throws RemoteException {
//            System.out.println("fileCount = " + fileCount + ", fileLength = " + fileLength);
//            averageFragment.updateFragment(fileLength/fileCount);
            Bundle bundle = new Bundle();
            Message message = new Message();
            message.what = Constant.MSG_UPDATE_AVERAGE_FRAGMENT;
            bundle.putLong("average", fileLength / fileCount);
            message.setData(bundle);
            mHandler.sendMessage(message);

            sendNotification(fileCount);
        }

        @Override
        public void updateBiggestFiles(List<BiggestFile> mList) throws RemoteException {
//            for(BiggestFile bf : mList){
//                System.out.println(bf.getFilePath() + ",   length = " + bf.getFileLength() );
//            }
            Bundle bundle = new Bundle();
            Message message = new Message();
            message.what = Constant.MSG_UPDATE_BIGGEST_FRAGMENT;
            bundle.putParcelableArrayList("biggest", (ArrayList<BiggestFile>)mList);
            message.setData(bundle);
            mHandler.sendMessage(message);

        }

        @Override
        public void updateExtensions(List<Extension> mList) throws RemoteException {
//            for(Extension extension : mList){
//                System.out.println(extension.getExtensionName() +  ", count = " + extension.getCount());
//            }
            Bundle bundle = new Bundle();
            Message message = new Message();
            message.what = Constant.MSG_UPDATA_EXTENSION_FRAGMENT;
            bundle.putParcelableArrayList("extensions", (ArrayList<Extension>)mList);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void scanFinished() throws RemoteException {
            mHandler.sendEmptyMessage(Constant.MSG_SCAN_FINISHED);
        }
    };

    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        biggestFilesFragment = BiggestFilesFragment.newInstance();
        averageFragment = AverageFragment.newInstance();
        extensionsFragment = ExtensionsFragment.newInstance();
        adapter.addFragment(biggestFilesFragment, "Biggest Files");
        adapter.addFragment(averageFragment, "Avg Size");
        adapter.addFragment(extensionsFragment, "Extensions");
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case Constant.MSG_START_PROGRESS:
                mProgressView.setProgress(0f);
                mProgressView.start();
//                mHandler.sendEmptyMessageDelayed(Constant.MSG_STOP_PROGRESS,  Constant.PROGRESS_INTERVAL);
                mHandler.sendEmptyMessageDelayed(Constant.MSG_UPDATE_PROGRESS, Constant.PROGRESS_UPDATE_INTERVAL);
                break;
            case Constant.MSG_UPDATE_PROGRESS:
                mProgressView.setProgress(mProgressView.getProgress() + 0.01f);
                if(mProgressView.getProgress() >= 1){
                    mProgressView.setProgress(0f);
                }
                if(STATUS == Constant.STATUS_START) {
                    mHandler.sendEmptyMessageDelayed(Constant.MSG_UPDATE_PROGRESS, Constant.PROGRESS_UPDATE_INTERVAL);
                }
                break;
            case Constant.MSG_STOP_PROGRESS:
                mProgressView.stop();
                break;
//            case Constant.MSG_UPDATE_PROGRESS:
//                mProgressView.setProgress(mProgressView.getProgress() + 0.01f);
//                break;
            case Constant.MSG_UPDATE_AVERAGE_FRAGMENT:
                long average = msg.getData().getLong("average");
                averageFragment.updateFragment(average);
                break;
            case Constant.MSG_UPDATE_BIGGEST_FRAGMENT:
                ArrayList biggestList = msg.getData().getParcelableArrayList("biggest");
                biggestFilesFragment.updateFragment(biggestList);
                break;
            case Constant.MSG_UPDATA_EXTENSION_FRAGMENT:
                ArrayList extensionList = msg.getData().getParcelableArrayList("extensions");
                extensionsFragment.updateFragment(extensionList);
                break;
            case Constant.MSG_SCAN_FINISHED:
                stopAll();
                mButton.setText("Start");
                STATUS = Constant.STATUS_STOP;
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("SD Card scan finished.")
//                        .setPositiveButton("OK", null)
//                        .show();
                Toast.makeText(MainActivity.this, "Scan finished", Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    //notification
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private int Notification_ID = 110;
    private Notification.Builder builder;

    private void initNotification(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder = new Notification.Builder(MainActivity.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
    }

    private void sendNotification(long fileCount){
        builder.setTicker(fileCount + " files processed.");
        builder.setContentTitle("Message");
        builder.setContentText(fileCount + " files processed.");

        Notification notification1 = builder.build();
        notificationManager.notify(Notification_ID, notification1);

    }

    @Override
    public void onBackPressed() {
        if(STATUS != Constant.STATUS_START){
            return;
        }
        try {
            registerCallback.changeStatus(Constant.STATUS_PAUSE);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mButton.setText("Resume");
        mHandler.sendEmptyMessage(Constant.MSG_STOP_PROGRESS);
        STATUS = Constant.STATUS_PAUSE;
    }
}
