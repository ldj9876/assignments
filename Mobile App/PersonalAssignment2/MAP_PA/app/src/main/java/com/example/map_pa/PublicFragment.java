package com.example.map_pa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.Iterator;


public class PublicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private DatabaseReference mPostReference;
    private StorageReference mStorageRef;
    ListViewAdapter mAdapter;

    String Username = "", profile = "";
    ListView posts;
    View v;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PublicFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPostReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        v = inflater.inflate(R.layout.fragment_public,container,false);
        posts = v.findViewById(R.id.public_List);

        mAdapter = new ListViewAdapter(getActivity());
        posts.setAdapter(mAdapter);
        getFirebaseDatabase();



        return v;
    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();

                TextView no_posts = v.findViewById(R.id.textView_public);
                Boolean Check = false;
                for (DataSnapshot postSnapshot : (dataSnapshot.child("posts/public")).getChildren()){

                    Check = true;

                    final Firebasepublic get = postSnapshot.getValue(Firebasepublic.class);
                    Username = get.username;
                    final FirebaseLogin info = dataSnapshot.child("user_data/"+Username).getValue(FirebaseLogin.class);
                    profile = info.profile;


                    if (profile.equals("default")){
                        if(get.img.equals("none")){
                            mAdapter.addItem(info.username,get.article,get.tags,null,null,get.id);
                            mAdapter.notifyDataSetChanged();
                        }
                        else{
                            StorageReference islandRef = mStorageRef.child("Posts/"+get.img);
                            islandRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                    mAdapter.addItem(info.username,get.article,get.tags,map,null,get.id);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                    else{
                        StorageReference profile_ref = mStorageRef.child("Profile/"+profile);
                        profile_ref.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                final Bitmap profile_img = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                if(get.img.equals("none")) {
                                    mAdapter.addItem(info.username,get.article,get.tags,null,profile_img,get.id);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else{
                                    StorageReference islandRef = mStorageRef.child("Posts/"+get.img);
                                    islandRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                            mAdapter.addItem(info.username,get.article,get.tags,map,profile_img,get.id);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                    }


                }
                if (Check) {
                    no_posts.setText("");
                }
                else{
                    no_posts.setText("No Posts");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.addValueEventListener(postListener);
    }
}


