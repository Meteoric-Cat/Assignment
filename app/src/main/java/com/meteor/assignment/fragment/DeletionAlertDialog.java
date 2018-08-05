package com.meteor.assignment.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import com.meteor.assignment.activity.R;

public class DeletionAlertDialog extends DialogFragment {
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dag_title))
                .setMessage(getString(R.string.dag_message))
                .setPositiveButton(R.string.dag_posButtom, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        clickHandler.handleDeletion();
                    }
                })
                .setNegativeButton(R.string.dag_nevButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }

    public static interface ClickHandler {
        public void handleDeletion();
    }
}
