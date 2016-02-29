package com.example.shon.boosttask8_navigation.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shon.boosttask8_navigation.MainActivity;
import com.example.shon.boosttask8_navigation.R;
import com.example.shon.boosttask8_navigation.entity.Sample;

import java.util.List;

public class DataRecAdapter extends RecyclerView.Adapter<DataRecAdapter.ViewHolder> {

    private List<Sample> mSamples;

    public DataRecAdapter(List<Sample> samples) {
        this.mSamples = samples;
    }

    @Override
    public DataRecAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_sample, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DataRecAdapter.ViewHolder holder, int position) {
        holder.mSampleName.setText(mSamples.get(position).getName());
        holder.mSampleX.setText("Axis X: " + String.valueOf(mSamples.get(position).getX()));
        holder.mSampleY.setText("Axis Y: " + String.valueOf(mSamples.get(position).getY()));
        holder.mSampleZ.setText("Axis Z: " + String.valueOf(mSamples.get(position).getZ()));
    }

    public void add(Sample sample){
        mSamples.add(MainActivity.FIRST_SAMPLE_POS, sample);
        notifyItemInserted(MainActivity.FIRST_SAMPLE_POS);
    }

    @Override
    public int getItemCount() {
        return mSamples.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSampleName;
        private TextView mSampleX;
        private TextView mSampleY;
        private TextView mSampleZ;

        public ViewHolder(View itemView) {
            super(itemView);
            mSampleName = (TextView) itemView.findViewById(R.id.tv_sample_name);
            mSampleX = (TextView) itemView.findViewById(R.id.tv_sample_x);
            mSampleX.setTextColor(Color.rgb(1, 168, 33));
            mSampleY = (TextView) itemView.findViewById(R.id.tv_sample_y);
            mSampleY.setTextColor(Color.rgb(220, 56, 71));
            mSampleZ = (TextView) itemView.findViewById(R.id.tv_sample_z);
            mSampleZ.setTextColor(Color.rgb(88, 144, 255));
        }
    }
}
