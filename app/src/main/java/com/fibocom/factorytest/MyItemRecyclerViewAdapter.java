package com.fibocom.factorytest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<MainContent.MainItem> mValues;
    private int mType;

    public MyItemRecyclerViewAdapter(List<MainContent.MainItem> items) {
        mValues = items;
        mType = 0;
    }

    public MyItemRecyclerViewAdapter(List<MainContent.MainItem> items, int type) {
        mValues = items;
        mType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (mType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_content, parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_info, parent, false);
        }
        view.setOnClickListener(v -> {
            TextView idView = v.findViewById(R.id.item_number);
            if (mType == 0) {
                if (idView.getText().equals("1")) {
                    Navigation.findNavController(idView)
                            .navigate(R.id.action_contentFragment_to_infoFragment);
                } else if (idView.getText().equals("2")) {
                    Navigation.findNavController(idView)
                            .navigate(R.id.action_contentFragment_to_networkFragment);
                } else if (idView.getText().equals("3")) {
                    Navigation.findNavController(idView)
                            .navigate(R.id.action_contentFragment_to_smsFragment);
                }
            } else {
                ;
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
        holder.mDetailView.setText(mValues.get(position).details);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDetailView;
        public MainContent.MainItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
            mDetailView = view.findViewById(R.id.detail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}