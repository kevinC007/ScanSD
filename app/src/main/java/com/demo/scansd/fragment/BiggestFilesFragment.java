package com.demo.scansd.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.scansd.R;
import com.demo.scansd.aidl.BiggestFile;
import com.demo.scansd.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class BiggestFilesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private BiggestFileAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<BiggestFile> mFileList ;

    public static BiggestFilesFragment newInstance() {
//        Bundle args = new Bundle();
        BiggestFilesFragment fragment = new BiggestFilesFragment();
//        args.putParcelableArrayList("files", files);
//        fragment.setArguments(args);
        return fragment;
    }

    public void updateFragment(List<BiggestFile> files){
        mFileList.clear();
        mFileList.addAll(files);
//        mAdapter = new BiggestFileAdapter(getActivity(), mFileList);
//        mRecyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

    }

    public List<BiggestFile> getBittestFile(){
        return mFileList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_biggest_files, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.biggestFileView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if(mFileList == null){
            mFileList = new ArrayList<BiggestFile>();
        }
        mAdapter = new BiggestFileAdapter(getActivity(), mFileList);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    public class BiggestFileAdapter extends RecyclerView.Adapter<BiggestFileAdapter.ViewHolder>{
        private List<BiggestFile> mList;
        private final TypedValue mTypedValue = new TypedValue();
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mFilePathView;
            public TextView mFileLengthView;

            public int position;

            public ViewHolder(View v) {
                super(v);
                mFilePathView = (TextView) v.findViewById(R.id.biggestFilePath);
                mFileLengthView = (TextView)v.findViewById(R.id.biggestFileLength);
            }
        }

        public BiggestFileAdapter(Context context, List<BiggestFile> mList){
            this.mList = mList;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        }

        @Override
        public BiggestFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_biggest_file, parent, false);
                // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            BiggestFile biggestFile = mList.get(position);
            holder.mFilePathView.setText("File Path : " + biggestFile.getFilePath());
            holder.mFileLengthView.setText("File length is " + biggestFile.getFileLength() + " bytes.");
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

}
