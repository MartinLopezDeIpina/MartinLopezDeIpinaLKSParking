package com.lksnext.parking.view.fragment;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.lksnext.parking.R;

import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.LoginErrorType;
import com.lksnext.parking.data.callbacks.LoginCallback;
import com.lksnext.parking.databinding.FragmentForgotPasswordBinding;
import com.lksnext.parking.viewmodel.RegisterViewModel;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ForgotPasswordFragment extends Fragment {
    private FragmentForgotPasswordBinding binding;
    private MaterialButton returnButton;
    private Button acceptButton;
    private NavController navController;
    private ProgressBar progressbar;
    public ForgotPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        bindReturnButton();
        bindAcceptButton();
        disableInputErrorStateFocused();

        progressbar = binding.progressbar;


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void bindReturnButton(){
        returnButton = binding.aceptarButton;
        returnButton.setOnClickListener(v -> navController.navigate(R.id.login_fragment));
    }

    private void bindAcceptButton() {
        acceptButton = binding.aceptarButton;
        acceptButton.setOnClickListener(v -> {
            progressbar.setVisibility(View.VISIBLE);
            String email = binding.emailText.getText().toString();
            DataRepository.getInstance().emailValid(email, new LoginCallback() {
                @Override
                public void onSuccess() {
                    DataRepository.getInstance().sendPasswordResetEmail(email, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                MotionToast.Companion.createColorToast(
                                        getActivity(),
                                        "Email enviado",
                                        "Email de recuperación correctamente enviado",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(getActivity(),R.font.robotobold)
                                );
                                progressbar.setVisibility(View.GONE);
                                navController.navigate(R.id.login_fragment);
                            } else {
                                Toast.makeText(getActivity(), "Error :- " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(LoginErrorType errorType) {
                    progressbar.setVisibility(View.GONE);
                    switch (errorType) {
                        case EMPTY_EMAIL:
                            binding.email.setError(LoginErrorType.EMPTY_EMAIL.getMessage());
                            break;
                        case INVALID_EMAIL:
                            binding.email.setError(LoginErrorType.INVALID_EMAIL.getMessage());
                            break;
                    }
                }
            });
        });
    }

    private void disableInputErrorStateFocused() {
        binding.email.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.email.setError(null);
            }
        });
    }


}
