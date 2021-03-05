package com.huy3999.schedules.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.huy3999.schedules.LoginActivity;
import com.huy3999.schedules.MainActivity;

public class LogoutFragment extends Fragment {

    private FirebaseAuth auth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        if(auth.getCurrentUser() == null){
            //User da login roi
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}