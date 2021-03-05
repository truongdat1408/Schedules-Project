package com.huy3999.schedules.fragment;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.navigation.NavigationView;
import com.huy3999.schedules.MainActivity;
import com.huy3999.schedules.R;

import java.util.ArrayList;

import akndmr.github.io.colorprefutil.ColorPrefUtil;
import petrov.kristiyan.colorpicker.ColorPicker;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {
    private static View root;
    private LinearLayout btnAccount, btnColorTheme, btnInfo, btnSupport;
    private String colorChoosed;
    private static final String PREFS_NAME = "YOUR_TAG";
    private static final String DATA_TAG = "DATA_TAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_settings_fragment, container, false);
        btnAccount = root.findViewById(R.id.account_settings);
        btnColorTheme = root.findViewById(R.id.color_theme_settings);
        btnInfo = root.findViewById(R.id.infomation_settings);
        btnSupport = root.findViewById(R.id.support_settings);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(container.getId(), new AccountFragment()).commit();
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog al = settingsDialog("Infomation","Schedule Application - version3 - Tr.CongDat,Ng.ThiTram,Ph.ThanhHuy,Ng.TienVan");
                al.show();
            }
        });

        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog al = settingsDialog("Support","If you need some helps, please call: 1900 6088 (Chi ong vang)");
                al.show();
            }
        });

        btnColorTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChooseColor(v);
            }
        });
        return root;
    }


    private AlertDialog settingsDialog(String title, String s) {
        //Dialog
        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        //Thiết lập tiêu đề
        b.setTitle(title);
        b.setMessage(s);
        // Nút Ok
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return b.create();
    }


    public void onChooseColor(View view) {
        openColorPicker();
    }

    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(getActivity());
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#000000");
        colors.add("#F44336");
        colors.add("#FF7F50");
        colors.add("#6495ED");
        colors.add("#40E0D0");

        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        colorChoosed = colors.get(position);
                        Log.d("oke", "onChooseColor: " + colorChoosed);
                        NavigationView navigationView = ((MainActivity)getActivity()).findViewById(R.id.nav_view);
                        View header = navigationView.getHeaderView(0);
                        LinearLayout linearLayout = header.findViewById(R.id.background_drawer);

                        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
                        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorChoosed)));
                        linearLayout.setBackgroundColor(Color.parseColor(colorChoosed));

                        //init
                        SharedPreferences mSettings = getActivity().getSharedPreferences(PREFS_NAME, 0);

                        //clear
                        SharedPreferences.Editor editor2 = mSettings.edit();
                        editor2.clear();
                        editor2.commit();

                        //commit
                        SharedPreferences.Editor editor = mSettings.edit();
                        editor.putString(DATA_TAG, colorChoosed);
                        editor.commit();

                        MainActivity.colorApp = colorChoosed;
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }
}