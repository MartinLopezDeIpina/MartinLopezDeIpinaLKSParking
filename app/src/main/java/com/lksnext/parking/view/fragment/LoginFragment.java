package com.lksnext.parking.view.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentLoginBinding;
import com.lksnext.parking.view.activity.LoginActivity;
import com.lksnext.parking.view.activity.MainActivity;
import com.lksnext.parking.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private View view;
    public LoginFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLoginViewModel(loginViewModel);

        binding.setLifecycleOwner(this);

        bindRegisterButton();
        bindLoginButton();
        bindDisableInputErrorStates();

        observeLogged();
        observeLoginError();

        setSpannedTitle();
        setUnderlinedForgotPassword();

        return view;
    }
    private void bindRegisterButton(){
        binding.createAccount.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.register_fragment);
        });
    }
    private void bindLoginButton(){
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString();
            String password = binding.passwordText.getText().toString();
            loginViewModel.loginUser(email, password);
        });
    }

    private void bindDisableInputErrorStates(){
        disableInputErrorStateFocused();
        disableInputErrorStateTextChanged();
    }
    private void disableInputErrorStateTextChanged(){
        binding.emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.email.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        binding.passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the text changes
                binding.password.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
    }
    private void disableInputErrorStateFocused(){
        binding.emailText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.email.setError(null);
            }
        });
        binding.passwordText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.password.setError(null);
            }
        });
    }

    private void observeLogged(){
        loginViewModel.isLogged().observe(getViewLifecycleOwner(), logged -> {
            if (logged != null) {
                if (logged) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void observeLoginError(){
        loginViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error == null) return;
            switch (error) {
                case EMPTY_EMAIL:
                case INVALID_EMAIL:
                case USER_NOT_FOUND:
                case EMAIL_NOT_VERIFIED:
                    binding.email.setError(error.getMessage());
                    break;
                case EMPTY_PASSWORD:
                case INVALID_PASSWORD:
                case WRONG_PASSWORD:
                    binding.password.setError(error.getMessage());
                    break;

                case UNKNOWN_ERROR:
                    binding.email.setError("Error en el login");
                    binding.password.setError("Error en el login");
                    break;
            }
        });
    }

    private void setSpannedTitle(){
        TextView title = view.findViewById(R.id.title);
        String text = title.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        Resources.Theme theme = getActivity().getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

        spannableString.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int secondWordStart = text.indexOf("S");
        spannableString.setSpan(new ForegroundColorSpan(color), secondWordStart, secondWordStart + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        title.setText(spannableString);
    }

    private void setUnderlinedForgotPassword(){
        TextView forgotPassButton = view.findViewById(R.id.forgotPassButton);
        SpannableString content = new SpannableString(forgotPassButton.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassButton.setText(content);
    }
}
