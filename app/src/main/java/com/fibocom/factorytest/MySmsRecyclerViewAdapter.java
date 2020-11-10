package com.fibocom.factorytest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MySmsRecyclerViewAdapter extends RecyclerView.Adapter<MySmsRecyclerViewAdapter.ViewHolder> {

    private final List<SmsItem> mValues;

    public MySmsRecyclerViewAdapter(List<SmsItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sms, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(R.id.tag_sms, viewHolder);
        view.setOnClickListener(v -> {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(v.getContext());
            ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_sms);
            normalDialog.setTitle(holder.mPhoneNumberView.getText());
            normalDialog.setMessage(holder.mContentView.getText());
            normalDialog.setPositiveButton("确定", null);
            normalDialog.show();
        });
        return viewHolder;
    }

    public void addItem(String phoneNumber, String content, String date) {
        int position = mValues.size();
        mValues.add(0, new SmsItem(position + 1, phoneNumber, content, date));
        notifyItemInserted(0);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(holder.mItem.id));
        holder.mPhoneNumberView.setText(holder.mItem.phoneNumber);
        holder.mDateView.setText(holder.mItem.date);
        holder.mContentView.setText(holder.mItem.content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mPhoneNumberView;
        public final TextView mDateView;
        public final TextView mContentView;
        public SmsItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mPhoneNumberView = view.findViewById(R.id.phone_number);
            mDateView = view.findViewById(R.id.date);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public static class SmsItem {
        public final int id;
        public final String phoneNumber;
        public final String content;
        public final String date;

        public SmsItem(int id, String phoneNumber, String content, String date) {
            this.id = id;
            this.phoneNumber = phoneNumber;
            this.content = content;
            this.date = date;
        }

        @Override
        public String toString() {
            return phoneNumber + "[" + content + "]";
        }
    }
}