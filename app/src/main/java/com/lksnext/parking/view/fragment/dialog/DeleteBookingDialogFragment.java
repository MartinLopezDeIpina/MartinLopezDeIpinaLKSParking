package com.lksnext.parking.view.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parking.R;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.viewmodel.BookViewModel;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.List;

public class DeleteBookingDialogFragment extends DialogFragment {
    MutableLiveData<Boolean> deleteClicked = new MutableLiveData<>();
    public DeleteBookingDialogFragment () {
    }
    public LiveData<Boolean> getDeleteClicked() {
        return deleteClicked;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_cancel_booking);
        builder.setMessage(R.string.dialog_cancel_booking_message)
                .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteClicked.setValue(true);
                    }
                })
                .setNegativeButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteClicked.setValue(false);
                    }
                });
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        return dialog;
    }
}
