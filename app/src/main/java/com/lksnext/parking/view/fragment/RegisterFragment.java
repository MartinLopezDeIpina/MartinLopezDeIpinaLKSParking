package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentProfileBinding;
import com.lksnext.parking.databinding.FragmentRegisterBinding;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.regex.Pattern;

public class RegisterFragment extends Fragment {

        private FragmentRegisterBinding binding;
        public RegisterFragment() {
            // Es necesario un constructor vacio
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

                binding = com.lksnext.parking.databinding.FragmentRegisterBinding.inflate(inflater, container, false);

                bindReturnButton();
                bindCreateAccountButton();
                bindDisableInputErrorStates();

                return binding.getRoot();
        }
        private void bindReturnButton(){
                binding.returning.setOnClickListener(v -> {
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.login_fragment);
                });
        }

        private void bindCreateAccountButton(){
                binding.createAccountButton.setOnClickListener(v -> {
                        if(validateInputs()){
                                NavController navController = Navigation.findNavController(v);
                                navController.navigate(R.id.login_fragment);
                        }
                });
        }
        private boolean validateInputs() {

            String user = binding.userInputText.getText().toString();
            String password = binding.passwordInputText.getText().toString();
            String checkPassword = binding.checkInputText.getText().toString();
            String email = binding.emailInputText.getText().toString();
            String phone = binding.phoneInputText.getText().toString();
            boolean checked = binding.termsCheck.isChecked();

            if(!validateUsername(user)){
                    return false;
            }
            if(!validatePassword(password)){
                    return false;
            }
            if(!validateCheckPassword(checkPassword, password)){
                    return false;
            }
            if(!validateEmail(email)){
                    return false;
            }
            if(!validatePhone(phone)){
                    return false;
            }

            if(!validateTermsChecked(checked)){
                    return false;
            }

            return true;
        }

        private boolean validateUsername(String user) {
                if (user.isEmpty()) {
                        binding.userInput.setError("Ingrese un nombre de usuario");
                        return false;
                }
                if (!Pattern.matches("^[a-zA-Z0-9]{1,}$", user)) {
                        binding.userInput.setError("El nombre de usuario debe tener solo letras y números");
                        return false;
                }
                return true;
        }
        private boolean validatePassword(String password) {
                if (password.isEmpty()) {
                        binding.passwordInput.setError("Ingrese una contraseña");
                        return false;
                }
                if(password.length() < 6){
                        binding.passwordInput.setError("La contraseña debe tener al menos 6 caracteres");
                        return false;
                }
                return true;
        }
        private boolean validateCheckPassword(String checkPassword, String password) {
                if (checkPassword.isEmpty()) {
                        binding.checkInput.setError("Confirme su contraseña");
                        return false;
                }
                if(!checkPassword.equals(password)){
                        binding.checkInput.setError("Las contraseñas deben coincidir");
                        return false;
                }
                return true;
        }
        private boolean validateEmail(String email) {
                if (email.isEmpty()) {
                        binding.emailInput.setError("Ingrese un correo electrónico");
                        return false;
                }
                if (!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email)) {
                        binding.emailInput.setError("Ingrese un correo electrónico válido");
                        return false;
                }
                return true;
        }
        private boolean validatePhone(String phone) {
                if (!phone.isEmpty() && !Pattern.matches("^[0-9]{10}$", phone)) {
                        binding.phoneInput.setError("El número de teléfono no es válido");
                        return false;
                }
                return true;
        }

        private boolean validateTermsChecked(boolean checked) {
                if (!checked) {
                        binding.termsError.setVisibility(View.VISIBLE);
                        return false;
                }
                return true;
        }


        private void bindDisableInputErrorStates(){
                disableInputErrorStateFocused();
                disableInputErrorStateTextChanged();
                disableCheckBoxErrorState();
        }

        private void disableInputErrorStateTextChanged() {
                binding.userInputText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // No action needed here
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                binding.userInput.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                // No action needed here
                        }
                });
                binding.passwordInputText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // No action needed here
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                binding.passwordInput.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                // No action needed here
                        }
                });
                binding.checkInputText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // No action needed here
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                binding.checkInput.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                // No action needed here
                        }
                });
                binding.emailInputText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // No action needed here
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                binding.emailInput.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                // No action needed here
                        }
                });
                binding.phoneInputText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                // No action needed here
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                binding.phoneInput.setError(null);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                                // No action needed here
                        }
                });
        }
        private void disableInputErrorStateFocused() {
                binding.userInputText.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) {
                                binding.userInput.setError(null);
                        }
                });
                binding.passwordInputText.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) {
                                binding.passwordInput.setError(null);
                        }
                });
                binding.checkInputText.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) {
                                binding.checkInput.setError(null);
                        }
                });
                binding.emailInputText.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) {
                                binding.emailInput.setError(null);
                        }
                });
                binding.phoneInputText.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus) {
                                binding.phoneInput.setError(null);
                        }
                });
        }
        private void disableCheckBoxErrorState() {
                binding.termsCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        binding.termsError.setVisibility(View.INVISIBLE);
                });
        }


}
