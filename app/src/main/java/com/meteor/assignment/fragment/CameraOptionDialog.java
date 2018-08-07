package com.meteor.assignment.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.meteor.assignment.activity.R;

public class CameraOptionDialog extends DialogFragment implements View.OnClickListener {            //or implement as a CreatingActivity's inner class
    private TextView tvTakePhoto, tvChoosePhoto;
    private ClickHandler clickHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            clickHandler = (ClickHandler) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_camera_option, container);

        tvTakePhoto = view.findViewById(R.id.tv_takePhoto);
        tvChoosePhoto = view.findViewById(R.id.tv_choosePhoto);

        tvTakePhoto.setOnClickListener(this);
        tvChoosePhoto.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_takePhoto: {
                clickHandler.handleTakingPhoto();
                dismiss();
                break;
            }
            case R.id.tv_choosePhoto: {
                clickHandler.handleChoosingPhoto();
                dismiss();
                break;
            }
        }
    }

    public static interface ClickHandler {
        public void handleTakingPhoto();
        public void handleChoosingPhoto();
    }
}
