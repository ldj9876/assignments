package com.example.personal_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImageAdapter extends BaseAdapter {
    private Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<GridViewItem> items = new ArrayList<GridViewItem>();
    public ImageAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate((R.layout.listview_item),null);
        ImageView img = (ImageView)view.findViewById(R.id.list_img);
        TextView tags = (TextView)view.findViewById(R.id.list_tags);
        TextView username = (TextView)view.findViewById(R.id.list_username);

        img.setImageBitmap(items.get(position).getImage());
        if(items.get(position).getTag().length()==0){
            tags.setVisibility(view.GONE);
        } else{
            tags.setText(items.get(position).getTag());
        }

        if(items.get(position).getUsername().length()==0){
            username.setVisibility(view.GONE);
        } else{
            username.setText(items.get(position).getUsername());
        }

        return view;
    }
//    public void addItem(Bitmap img, String tag, String username){
//        GridViewItem info = new GridViewItem(img,tag,username);
//        items.add(info);
//    }

    public void addItem(Bitmap img, String tag, String username,String id){
        GridViewItem info = new GridViewItem(img,tag,username,id);
        items.add(info);
        Collections.sort(items,cmp2);
    }

    public void clear() { items.clear();}

    public void set_value(int pos, double value) {
        items.get(pos).setDistance(value);
    }

    public static Comparator<GridViewItem> cmp = new Comparator<GridViewItem>() {
        @Override
        public int compare(GridViewItem o1, GridViewItem o2) {
            int result;
            if (o1.getDistance() < o2.getDistance())
                result = -1;
            else if (o1.getDistance() == o2.getDistance())
                result = 0;
            else
                result = 1;

            return result;
        }
    };
    public static Comparator<GridViewItem> cmp2 = new Comparator<GridViewItem>() {
        @Override
        public int compare(GridViewItem o1, GridViewItem o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };
    public void sorting() {
        Collections.sort(items,cmp);
    }

}
