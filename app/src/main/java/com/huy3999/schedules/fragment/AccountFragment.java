package com.huy3999.schedules.fragment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.huy3999.schedules.LoginActivity;
import com.huy3999.schedules.R;
import com.huy3999.schedules.helper.Constant;
import com.squareup.picasso.Picasso;


public class AccountFragment extends Fragment {
    private static View root;
    private int idContainer;
    private FirebaseAuth auth;
    private String email;
    private String name;
    private String phone;
    private EditText txtEmail, txtName, txtPhone;
    private Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_account_fragment, container, false);
        idContainer = container.getId();
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();
        name = auth.getCurrentUser().getDisplayName();
        phone = auth.getCurrentUser().getPhoneNumber();
        txtEmail = root.findViewById(R.id.mail_account);
        txtName = root.findViewById(R.id.name_account);
        txtPhone = root.findViewById(R.id.phone_account);
        btnLogout = root.findViewById(R.id.btn_logout_account);

        txtName.setText(name);
        txtPhone.setText(phone);
        txtEmail.setText(email);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                if(auth.getCurrentUser() == null){
                    //User da login roi
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            }
        });
        ImageView imageView = root.findViewById(R.id.avatar_account);
        Picasso.with(getContext()).load(auth.getCurrentUser().getPhotoUrl().toString()).into(imageView);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Button back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(idContainer, new SettingsFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}