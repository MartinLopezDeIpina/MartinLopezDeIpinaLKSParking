package com.lksnext.parking.view.fragment;

import android.os.Bundle;
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

                return binding.getRoot();
        }
        private void bindReturnButton(){
                binding.returning.setOnClickListener(v -> {
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.login_fragment);
                });
        }

}
