package com.fibocom.factorytest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fibocom.factorytest.SmsContent.SmsItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SmsItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySmsRecyclerViewAdapter extends RecyclerView.Adapter<MySmsRecyclerViewAdapter.ViewHolder> {

    private final List<SmsItem> mValues;

    public MySmsRecyclerViewAdapter(List<SmsItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sms, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mPhoneNumberView.setText(mValues.get(position).phoneNumber);
        holder.mDateView.setText(mValues.get(position).date);
        holder.mContentView.setText(mValues.get(position).content);
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
            mIdView = (TextView) view.findViewById(R.id.item_number);
            mPhoneNumberView = view.findViewById(R.id.phone_number);
            mDateView = view.findViewById(R.id.date);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}