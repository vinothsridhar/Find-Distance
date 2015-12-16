package com.sri.finddistance.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sri.finddistance.R;
import com.sri.finddistance.gson.Directions;

import java.util.ArrayList;

/**
 * Created by sridhar on 16/12/15.
 */
public class PathListAdapter extends RecyclerView.Adapter<PathListAdapter.ViewHolder> {

    private ArrayList<Directions.Step> pathList = new ArrayList<Directions.Step>();

    public PathListAdapter(ArrayList<Directions.Step> pathList) {
        this.pathList = pathList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.htmlInstructions.setText(Html.fromHtml(pathList.get(position).html_instructions));
        holder.distance.setText(pathList.get(position).distance.text.toUpperCase());
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView htmlInstructions;
        private TextView distance;

        public ViewHolder(View itemView) {
            super(itemView);
            htmlInstructions = (TextView) itemView.findViewById(R.id.htmlInstructions);
            distance = (TextView) itemView.findViewById(R.id.distance);
        }
    }

}
