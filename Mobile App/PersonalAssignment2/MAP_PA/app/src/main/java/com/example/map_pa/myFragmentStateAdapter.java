package com.example.map_pa;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class myFragmentStateAdapter extends FragmentStateAdapter{
    String Username;

    public myFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, String username) {
        super(fragmentActivity);
        this.Username = username;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                PersonalFragment personal = new PersonalFragment();
                Bundle bundle = new Bundle();
                bundle.putString("username",Username);
                personal.setArguments(bundle);
                return personal;
            case 1:
                Fragment public_frag = new PublicFragment();
                return public_frag;


        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }




}