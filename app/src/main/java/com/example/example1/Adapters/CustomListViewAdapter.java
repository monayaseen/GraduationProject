package com.example.example1.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.example1.R;
import com.example.example1.modal.Items;

import java.util.ArrayList;
import java.util.List;

public class CustomListViewAdapter extends ArrayAdapter<Items> implements Filterable {

    Context context;
    List<Items> itemsModelsl;
    List<Items> itemsModelListFiltered;

    public CustomListViewAdapter(Context context, int resourceId,
                                 List<Items> items) {
        super(context, resourceId, items);
        this.context = context;
        this.itemsModelsl = items;
        this.itemsModelListFiltered = itemsModelsl;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtPrice;
    }

    @Override
    public int getCount() {
        return itemsModelListFiltered.size();
    }

    @Override
    public Items getItem(int position) {
        return itemsModelListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Items rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_row, null);
            holder = new ViewHolder();
            holder.txtPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.tv_product);
            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtPrice.setText(rowItem.getPrice());
        holder.txtTitle.setText(rowItem.getProd_name());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.count = itemsModelsl.size();
                    filterResults.values = itemsModelsl;

                } else {
                    List<Items> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for (Items itemsModel : itemsModelsl) {
                        if (itemsModel.getProd_name().toLowerCase().contains(searchStr)) {
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }


                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                itemsModelListFiltered = (List<Items>) results.values;
                notifyDataSetChanged();

            }
        };
    }

}

