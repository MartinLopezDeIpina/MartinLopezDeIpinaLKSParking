package com.lksnext.parking.view.activity;

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
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parking.viewmodel.LoginViewModel;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz login (layout)
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de login
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        //Acciones a realizar cuando el usuario clica el boton de login
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString();
            String password = binding.passwordText.getText().toString();
            loginViewModel.loginUser(email, password);
        });

        //Acciones a realizar cuando el usuario clica el boton de crear cuenta (se cambia de pantalla)
        binding.createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        bindDisableInputErrorStates();
        observeLogged();
        observeLoginError();

        setSpannedTitle();
        setUnderlinedForgotPassword();
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
        loginViewModel.isLogged().observe(this, logged -> {
            if (logged != null) {
                if (logged) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void observeLoginError(){
        loginViewModel.getErrorMessage().observe(this, error -> {
            if (error == null) return;
            switch (error) {
                case EMPTY_EMAIL:
                case INVALID_EMAIL:
                case USER_NOT_FOUND:
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
        TextView title = findViewById(R.id.title);
        String text = title.getText().toString();
        SpannableString spannableString = new SpannableString(text);

        Resources.Theme theme = getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;

        spannableString.setSpan(new ForegroundColorSpan(color), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int secondWordStart = text.indexOf("S");
        spannableString.setSpan(new ForegroundColorSpan(color), secondWordStart, secondWordStart + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        title.setText(spannableString);
    }

    private void setUnderlinedForgotPassword(){
        TextView forgotPassButton = findViewById(R.id.forgotPassButton);
        SpannableString content = new SpannableString(forgotPassButton.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotPassButton.setText(content);
    }
}