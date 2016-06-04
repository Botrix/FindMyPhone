package com.edl.findmyphone.fragment;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.edl.findmyphone.R;
import com.edl.findmyphone.MainActivity;
import com.edl.findmyphone.receiver.MyAdmin;
import com.edl.findmyphone.widget.NormalTopBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFra extends Fragment implements View.OnClickListener {
    public static DevicePolicyManager devicePolicyManager;
    ComponentName adminComponent;
    public static boolean isAdminActive;

    RelativeLayout rl_settings;
    RelativeLayout rl_find ;

    NormalTopBar mTopBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fra_main, container, false);

        rl_settings = (RelativeLayout) view.findViewById(R.id.rl_settings);
        rl_find = (RelativeLayout) view.findViewById(R.id.rl_find);

        mTopBar = (NormalTopBar) view.findViewById(R.id.topbar);
        mTopBar.setVisibility(View.VISIBLE);
        mTopBar.setTitle("手机找回");
        mTopBar.setBackVisibility(false);

        rl_settings.setOnClickListener(this);
        rl_find.setOnClickListener(this);


        devicePolicyManager = (DevicePolicyManager)
                getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(getActivity(),MyAdmin.class);
        isAdminActive = devicePolicyManager.isAdminActive(adminComponent);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i("MainFra->onStart", "Whether active admin");
        if (!isAdminActive) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "使用手机找回功能，需要激活设备管理器");
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentActivity activity = getActivity();
        switch (v.getId()) {
            case R.id.rl_settings:
                ((MainActivity)activity).onStartSettingsFra();
                Log.i("MainFragment", "onStartMayFindFra()");
                break;
            case R.id.rl_find:
                ((MainActivity)activity).onStartFindFra();
                Log.i("MainFragment", "onStartFindFra()");
                break;
            default:
                break;
        }
    }



}
