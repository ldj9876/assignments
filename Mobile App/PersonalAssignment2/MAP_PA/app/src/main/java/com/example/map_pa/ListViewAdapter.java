package com.example.map_pa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListViewAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    private ArrayList<ListViewitem> listViewitems = new ArrayList<ListViewitem>();

    public ListViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listViewitems.size();
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public ListViewitem getItem(int position) {
        return listViewitems.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_item, null);
        ImageView profile = (ImageView)view.findViewById(R.id.list_profile);
        TextView username = (TextView)view.findViewById(R.id.list_username);
        TextView article = (TextView)view.findViewById(R.id.list_article);
        TextView tags = (TextView)view.findViewById(R.id.list_tags);
        ImageView img = (ImageView)view.findViewById(R.id.list_img);
//        profile.setImageDrawable(listViewitems.get(position).getprofile());
        username.setText(listViewitems.get(position).getusername());
        article.setText(listViewitems.get(position).getarticle());
        tags.setText(listViewitems.get(position).gettags());
//        img.setImageDrawable(listViewitems.get(position).getpost_img());
//        ((ViewGroup) img.getParent()).removeView(img);
        if (listViewitems.get(position).getpost_img()!=null){
            img.setImageBitmap(listViewitems.get(position).getpost_img());
        }
        else{
            ((ViewGroup) img.getParent()).removeView(img);
        }
        if (listViewitems.get(position).getprofile()!=null){
            profile.setImageBitmap(listViewitems.get(position).getprofile());
        }

        return view;
    }
    public void addItem(String username, String article, String tags, Bitmap post_img, Bitmap profile, String id){
        ListViewitem info = new ListViewitem(username,article,tags, post_img, profile,id);
        listViewitems.add(info);
        Collections.sort(listViewitems,cmp);

    }
    public void clear(){
        listViewitems.clear();
    }

    public static Comparator<ListViewitem> cmp = new Comparator<ListViewitem>() {
        @Override
        public int compare(ListViewitem o1, ListViewitem o2) {
            return o1.getid().compareTo(o2.getid());
        }
    };



}

