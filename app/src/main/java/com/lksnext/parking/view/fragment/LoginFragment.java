package com.lksnext.parking.view.fragment;


import static android.app.Activity.RESULT_OK;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lksnext.parking.R;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.RegisterErrorType;
import com.lksnext.parking.data.callbacks.RegisterCallback;
import com.lksnext.parking.databinding.FragmentLoginBinding;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.view.activity.MainActivity;
import com.lksnext.parking.viewmodel.LoginViewModel;

import java.util.Objects;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private TextView forgotPassButton;
    private View view;


    public LoginFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);

        binding.setLoginViewModel(loginViewModel);

        binding.setLifecycleOwner(this);

        bindRegisterButton();
        bindLoginButton();
        bindDisableInputErrorStates();
        bindForgotPassword();

        observeLogged();
        observeLoginError();

        setSpannedTitle();
        setUnderlinedForgotPassword();

        bindGoolgeAuthenticationButton();

        return view;
    }

    private void bindGoolgeAuthenticationButton(){
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("298070462578-1gp367gffjv4kfjp1u4va8955nnspq2q.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        binding.googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof SignInHandler) {
                    binding.loginTopProgressbar.setVisibility(View.VISIBLE);
                    ((SignInHandler) getActivity()).startOneTapSignIn(signInRequest);
                }
            }
        });
    }
    public interface SignInHandler {
        void startOneTapSignIn(BeginSignInRequest signInRequest);
    }


    @Override
    public void onResume() {
        super.onResume();
        binding.loginTopProgressbar.setVisibility(View.GONE);
    }

    private void bindRegisterButton(){
        binding.createAccount.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.register_fragment);
        });
    }
    private void bindLoginButton(){
        binding.loginButton.setOnClickListener(v -> {
            binding.loginTopProgressbar.setVisibility(View.VISIBLE);
            String email = binding.loginEmailText.getText().toString();
            String password = binding.loginPasswordText.getText().toString();
            loginViewModel.loginUser(email, password);
        });
    }
    private void bindForgotPassword(){
        forgotPassButton = view.findViewById(R.id.forgotPassButton);
        forgotPassButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.forgot_password_fragment);
        });
    }

    private void bindDisableInputErrorStates(){
        disableInputErrorStateFocused();
        disableInputErrorStateTextChanged();
    }
    private void disableInputErrorStateTextChanged(){
        binding.loginEmailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.inputLoginEmail.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        binding.loginPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear the error when the text changes
                binding.loginInputPassword.setError(null);
            }
            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });
    }
    private void disableInputErrorStateFocused(){
        binding.loginEmailText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.inputLoginEmail.setError(null);
            }
        });
        binding.loginPasswordText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.loginInputPassword.setError(null);
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
            binding.loginTopProgressbar.setVisibility(View.GONE);
            if (error == null) return;
            switch (error) {
                case EMPTY_EMAIL:
                case INVALID_EMAIL:
                case USER_NOT_FOUND:
                case EMAIL_NOT_VERIFIED:
                    binding.inputLoginEmail.setError(error.getMessage());
                    break;
                case EMPTY_PASSWORD:
                case INVALID_PASSWORD:
                case WRONG_PASSWORD:
                    binding.loginInputPassword.setError(error.getMessage());
                    break;

                case UNKNOWN_ERROR:
                    binding.inputLoginEmail.setError("Error en el login");
                    binding.loginInputPassword.setError("Error en el login");
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
