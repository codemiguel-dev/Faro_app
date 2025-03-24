package com.pack.faro.filters;

import android.widget.Filter;

import com.pack.faro.adapters.AdapterUser;
import com.pack.faro.model.ModelUser;

import java.util.ArrayList;

public class FilterUser extends Filter {

    ArrayList<ModelUser>filterList;

    AdapterUser adapterUser;

    public FilterUser(ArrayList<ModelUser> filterList, AdapterUser adapterUser){
        this.filterList = filterList;
        this.adapterUser = adapterUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelUser> filteredModel = new ArrayList<>();

            for (int i=0; i<filterList.size(); i++){
                if (filterList.get(i).getEmail().toUpperCase().contains(constraint)){
                    filteredModel.add(filterList.get(i));
                }
            }

            results.count = filteredModel.size();
            results.values = filteredModel;

        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    protected void publishResults(CharSequence constraint, FilterResults results){
        adapterUser.userArrayList = (ArrayList<ModelUser>)results.values;
        adapterUser.notifyDataSetChanged();
    }
}
