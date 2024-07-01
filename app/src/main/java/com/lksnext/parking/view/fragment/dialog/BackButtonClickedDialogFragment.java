package com.lksnext.parking.view.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lksnext.parking.R;
public class BackButtonClickedDialogFragment extends DialogFragment {
    MutableLiveData<Boolean> backClicked = new MutableLiveData<>();
    public BackButtonClickedDialogFragment () {
    }
    public LiveData<Boolean> getBackClicked() {
        return backClicked;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_message)
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        backClicked.setValue(true);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        backClicked.setValue(false);
                    }
                });
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        return dialog;
    }
}
