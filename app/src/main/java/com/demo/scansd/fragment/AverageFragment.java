package com.demo.scansd.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.scansd.R;


public class AverageFragment extends Fragment{
    private TextView averageView ;
    public static AverageFragment newInstance() {
        AverageFragment fragment = new AverageFragment();
        return fragment;
    }

    public void updateFragment(long average){
//        System.out.println("average = " + average);
//        Bundle bundle = new Bundle();
//        Message message = new Message();
//        message.what = 1;
//        bundle.putLong("average", average);
//        message.setData(bundle);
//        mHandler.sendMessage(message);
        averageView.setText("Average: " + average + " bytes");
    }

    public String getAverageText(){
        return averageView.getText().toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_average, null);
        averageView = (TextView) view.findViewById(R.id.averageView);
        averageView.setText("Average: ");
//        tvInfo.setText(getArguments().getString("info"));
//        tvInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v,"hello",Snackbar.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }

//    public Handler mHandler=new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case 1:
//                    long message = msg.getData().getLong("average");
//                    averageView.setText("Average: " + message + " bytes");
//                    break;
//            }
//        }
//    };


}
