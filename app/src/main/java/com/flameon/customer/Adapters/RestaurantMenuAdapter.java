package com.flameon.customer.Adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.flameon.customer.Constants.Config;
import com.flameon.customer.Models.RestaurantParentModel;
import com.flameon.customer.ActivitiesAndFragments.RestaurantMenuItemsFragment;
import com.flameon.customer.Models.RestaurantChildModel;
import com.flameon.customer.R;


import java.util.ArrayList;

/**
 * Created by qboxus on 10/18/2019.
 */

public class RestaurantMenuAdapter extends BaseExpandableListAdapter implements Filterable {
    Context context;
    ArrayList<RestaurantParentModel>ListTerbaru;
    ArrayList<ArrayList<RestaurantChildModel>> ListChildTerbaru;
    private   ArrayList<RestaurantParentModel> mFilteredList;
    public static boolean FLAG_OUT_OF_ORDER;


    public RestaurantMenuAdapter (Context context, ArrayList<RestaurantParentModel> ListTerbaru, ArrayList<ArrayList<RestaurantChildModel>> ListChildTerbaru){
        this.context=context;
        this.ListTerbaru=ListTerbaru;
        this.ListChildTerbaru=ListChildTerbaru;
        this.mFilteredList = ListTerbaru;
    }
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }


    @Override
    public RestaurantChildModel getChild(int groupPosition, int childPosition) {
        return ListChildTerbaru.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        RestaurantChildModel childTerbaru = getChild(groupPosition, childPosition);
        RestaurantMenuAdapter.ViewHolder holder= null;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_restaurant_child, null);

            holder=new RestaurantMenuAdapter.ViewHolder();
            holder.image=(SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.title_name_child=(TextView)convertView.findViewById(R.id.title_name_child);
            holder.sub_title_name_child = convertView.findViewById(R.id.sub_title_name_child);
            holder.price_tv = convertView.findViewById(R.id.price_tv);
            holder.order_status_tv = convertView.findViewById(R.id.order_status_tv);



            convertView.setTag(holder);

        }
        else{
            holder=(RestaurantMenuAdapter.ViewHolder)convertView.getTag();
        }
        String get_order_status = childTerbaru.order_detail;
        String get_symbol = childTerbaru.currency_symbol;
        if (get_order_status.equalsIgnoreCase("1"))
        {
            holder.price_tv.setText("Out of order");
            holder.price_tv.setTextSize(11);
            FLAG_OUT_OF_ORDER = true;
        }
        else {
            holder.price_tv.setText(get_symbol+childTerbaru.price);
            holder.price_tv.setTextSize(14);
            FLAG_OUT_OF_ORDER = false;
        }

        if(childTerbaru.image!=null && !childTerbaru.image.equals("")){
            holder.image.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(Config.imgBaseURL+childTerbaru.image);
            holder.image.setImageURI(uri);
        }else {
            holder.image.setVisibility(View.INVISIBLE);
        }

        String title = childTerbaru.child_title.replaceAll("&amp;", "&");
        holder.title_name_child.setText(title);
        String subtitle = childTerbaru.child_sub_title.replaceAll("&amp;", "&");
        holder.sub_title_name_child.setText(subtitle);


        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return ListChildTerbaru.get(groupPosition).size();
    }@Override
    public RestaurantParentModel getGroup(int groupPosition) {
        return mFilteredList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mFilteredList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        RestaurantParentModel terbaruModel =  getGroup(groupPosition);
        RestaurantMenuAdapter.ViewHolder holder= null;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.row_item_restaurant_parent, null);

            holder=new RestaurantMenuAdapter.ViewHolder();
            holder.image=(SimpleDraweeView) convertView.findViewById(R.id.image);
            holder.title_name=(TextView)convertView.findViewById(R.id.title_name);
            holder.sub_title_name=(TextView)convertView.findViewById(R.id.sub_title_name);
            holder.mainDiv_Parent = (RelativeLayout)convertView.findViewById(R.id.mainDiv_Parent);
            convertView.setTag(holder);

        }

        else{
            holder=(RestaurantMenuAdapter.ViewHolder)convertView.getTag();
        }

        holder.title_name.setText(terbaruModel.getTitle());
        if(RestaurantMenuItemsFragment.FLAG_SUGGESTION){
            holder.sub_title_name.setVisibility(View.GONE);
            holder.mainDiv_Parent.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite));
        }
        else {
            holder.sub_title_name.setText(terbaruModel.getSub_title());
        }


        if(terbaruModel.image!=null && !terbaruModel.image.equals("")){
            holder.image.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(Config.imgBaseURL+terbaruModel.image);
            holder.image.setImageURI(uri);
        }else {
            holder.image.setVisibility(View.INVISIBLE);
        }



        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = ListTerbaru;
                } else {
                    ArrayList<RestaurantParentModel> filteredList = new ArrayList<>();
                    for (RestaurantParentModel row : ListTerbaru) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<RestaurantParentModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };


    }


    static class ViewHolder{
        TextView title_name,sub_title_name,title_name_child,sub_title_name_child,price_tv,order_status_tv;
        RelativeLayout mainDiv_Parent;
        SimpleDraweeView image;
    }


}