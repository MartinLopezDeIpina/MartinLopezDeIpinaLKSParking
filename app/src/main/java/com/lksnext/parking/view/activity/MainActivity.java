package com.lksnext.parking.view.activity;

import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.parking.R;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.data.PlazaCallback;
import com.lksnext.parking.data.ReservaCallback;
import com.lksnext.parking.data.UserCallback;
import com.lksnext.parking.databinding.ActivityMainBinding;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.List;

public class MainActivity extends BaseActivity {

    MainViewModel viewModel;
    BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    DataBaseManager dataBaseManager = DataBaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz main (layout)
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setMainViewModel(viewModel);
        setCurrentUserDataFromDB();

        setContentView(binding.getRoot());

        //Con el NavigationHost podremos movernos por distintas pestañas dentro de la misma pantalla
        NavHostFragment navHostFragment =
            (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.flFragment);
        navController = navHostFragment.getNavController();

        //Asignamos los botones de navegacion que se encuentran en la vista (layout)
        bottomNavigationView = binding.bottomNavigationView;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //Dependendiendo que boton clique el usuario de la navegacion se hacen distintas cosas
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.newres) {
                navController.navigate(R.id.mainFragment);
                return true;
            } else if (itemId == R.id.reservations) {
                navController.navigate(R.id.bookFragment);
                return true;
            } else if (itemId == R.id.person) {
                navController.navigate(R.id.profileFragment);
                return true;
            }
            return false;
        });


        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorSurfaceContainerLow, typedValue, true);
        int color = typedValue.data;

        // Establece el color de la barra de navegación
        getWindow().setNavigationBarColor(color);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    private void setCurrentUserDataFromDB(){
        setProfileData();
        setSpotData();
        setBookingData();
    }
    private void setProfileData(){
        dataBaseManager.getCurrenUser(new UserCallback() {
            @Override
            public void onCallback(Usuario usuario) {
                viewModel.setUser(usuario);
            }
        });
    }
    private void setSpotData(){
        dataBaseManager.getParkingSpots(new PlazaCallback() {
            @Override
            public void onCallback(List<Plaza> plazas) {
                viewModel.setListaPlazas(plazas);
            }
        });
    }
    private void setBookingData(){
        dataBaseManager.getCurrentUserBookings(new ReservaCallback() {
            @Override
            public void onCallback(List<Reserva> reservas, List<ReservaCompuesta> compuestas) {
                viewModel.setListaReservas(reservas, compuestas);
            }
        });
    }
}