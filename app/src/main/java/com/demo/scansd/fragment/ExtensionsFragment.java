package com.demo.scansd.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.scansd.R;
import com.demo.scansd.aidl.Extension;
import com.demo.scansd.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class ExtensionsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ExtensionAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Extension> mExtensionList ;

    public static ExtensionsFragment newInstance() {
        ExtensionsFragment fragment = new ExtensionsFragment();
        fragment.initFragmentData();
        return fragment;
    }

    public void updateFragment(List<Extension> extensions){
        mExtensionList.clear();
        mExtensionList.addAll(extensions);
        mAdapter.notifyDataSetChanged();

    }

    public List<Extension> getExtensionList(){
        return mExtensionList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extensions, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.extensionView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
//        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), onItemClickListener));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private void initFragmentData(){
        if(mExtensionList == null){
            mExtensionList = new ArrayList<Extension>();
        }
        mAdapter = new ExtensionAdapter(getActivity(), mExtensionList);
    }

    public class ExtensionAdapter extends RecyclerView.Adapter<ExtensionAdapter.ViewHolder>{
        private List<Extension> mList;
//        private final TypedValue mTypedValue = new TypedValue();
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mNameView;
            public TextView mCountView;

            public int position;

            public ViewHolder(View v) {
                super(v);
                mNameView = (TextView) v.findViewById(R.id.extensionNameView);
                mCountView = (TextView)v.findViewById(R.id.extensionCountView);
            }
        }

        public ExtensionAdapter(Context context, List<Extension> mList){
            this.mList = mList;
//            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        }

        @Override
        public ExtensionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_extension, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            Extension extension = mList.get(position);
            holder.mNameView.setText("Extension name: " + extension.getExtensionName());
            holder.mCountView.setText("Extension count: " + extension.getCount());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mList.size();
        }
    }
}
