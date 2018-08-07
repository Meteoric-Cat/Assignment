package com.meteor.assignment.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meteor.assignment.activity.R;
import com.meteor.assignment.configuration.ActivityConfiguration;

public class BackgroundColorDialog extends DialogFragment implements OnClickListener {               //or a CreatingActivity's inner class
    private ImageView ivColorWhite, ivColorOrange, ivColorSeaGreen, ivColorLightBlue;               //ImageView[] ivColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_background_color, container, false);

        ivColorWhite = view.findViewById(R.id.iv_colorWhite);
        ivColorOrange = view.findViewById(R.id.iv_colorOrange);
        ivColorLightBlue = view.findViewById(R.id.iv_colorLightBlue);
        ivColorSeaGreen = view.findViewById(R.id.iv_colorSeaGreen);

        ivColorWhite.setOnClickListener(this);
        ivColorOrange.setOnClickListener(this);
        ivColorSeaGreen.setOnClickListener(this);
        ivColorLightBlue.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        getActivity().getWindow().setBackgroundDrawable(view.getBackground());
        ActivityConfiguration.getInstance().windowBackground = view.getBackground();
        this.dismiss();
    }
}
