package com.lksnext.parking.view.activity;

import android.os.Bundle;
import android.util.TypedValue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.parking.R;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.databinding.ActivityMainBinding;
import com.lksnext.parking.domain.Plaza;
import com.lksnext.parking.domain.Reserva;
import com.lksnext.parking.domain.ReservaCompuesta;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.view.fragment.DeleteBookingDialogFragment;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.List;

public class MainActivity extends BaseActivity implements OnEditClickListener, OnDeleteClickListener{

    MainViewModel mainViewModel;
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
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setMainViewModel(mainViewModel);
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

        setBookFragmentNavigationListener();
        setMainFragmentNavigationListener();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onEditClick(List<Reserva> reservations) {
       //todo
    }

    @Override
    public void onDeleteClick(String reservationID, boolean esCompuesta) {

        DeleteBookingDialogFragment deleteDialog = new DeleteBookingDialogFragment();
        deleteDialog.show(getSupportFragmentManager(), "deleteDialog");

        deleteDialog.getDeleteClicked().observe(this, result -> {
            LiveData<Boolean> bookingDeleted;
            if(result){
                if(esCompuesta){
                    bookingDeleted = mainViewModel.deleteReservaCompuestaAndChilds(reservationID);
                }else{
                    bookingDeleted = mainViewModel.deleteBooking(reservationID);
                }
                bookingDeleted.observeForever(bookingDeletedResult -> {
                    if(bookingDeletedResult){
                        mainViewModel.setBookingModified(reservationID);
                        mainViewModel.updateReservas();
                    }
                });
            }
        });
    }



    private void setCurrentUserDataFromDB(){
        setProfileData();
        setSpotData();
        setBookingData();
    }
    private void setProfileData(){
        LiveData<Usuario> userLiveData = dataBaseManager.getCurrenUser();
        userLiveData.observe(this, usuario -> {
            mainViewModel.setUser(usuario);
        });
    }
    private void setSpotData(){
        LiveData<List<Plaza>> plazaLiveData = dataBaseManager.getParkingSpots();
        plazaLiveData.observe(this, plazas -> {
            mainViewModel.setListaPlazas(plazas);
        });
    }
    private void setBookingData(){
        dataBaseManager.getCurrentUserBookings().observe(this, result -> {
            List<Reserva> reservas = (List<Reserva>) result[0];
            List<ReservaCompuesta> compuestas = (List<ReservaCompuesta>) result[1];
            mainViewModel.setListaReservas(reservas, compuestas);
        });
    }

    private void setBookFragmentNavigationListener(){
        mainViewModel.getNavigateToBookingFragment().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                navigateToBookingHistory();
                mainViewModel.navigateToBookingFragment(false, mainViewModel.getBookingNavigationPosition());
            }
        });
    }
    private void navigateToBookingHistory(){
        navController.navigate(R.id.bookFragment);
        bottomNavigationView.setSelectedItemId(R.id.reservations);
    }

    private void setMainFragmentNavigationListener(){
        mainViewModel.getNavigateToMainFragment().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                navigateToMainFragment();
                mainViewModel.navigateToMainFragment(false);
            }
        });
    }
    public void navigateToMainFragment(){
        navController.navigate(R.id.mainFragment);
        bottomNavigationView.setSelectedItemId(R.id.newres);
    }

}