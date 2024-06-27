package com.lksnext.parking.view.fragment;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.FragmentMainBinding;
import com.lksnext.parking.domain.Parking;
import com.lksnext.parking.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;

import app.futured.donut.DonutProgressView;
import app.futured.donut.DonutSection;
import app.futured.donut.DonutStrokeCap;


public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private MainViewModel mainViewModel;
    private View view;
    private DonutProgressView dpvChart;
    private Parking parking = Parking.getInstance();
    private Chip chipCar, chipMoto, chipElectric, chipSpecial;
    private TextView donutText;
    private ProgressBar progressBar;

    public MainFragment() {
        // Es necesario un constructor vacio
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        BookContainerFragment bookContainerFragment = new BookContainerFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.book_container, bookContainerFragment);
        transaction.commit();



        bindProgressBar();
        bindChips();
        bindAddButton();
        bindViewMoreButton();
        bindDonut();
        bindDonutProgressBar();

        return view;
    }

    private void bindProgressBar(){
        progressBar = binding.donutProgressBar;
        progressBar.setVisibility(View.VISIBLE);
    }

    private void bindChips(){
        chipCar = binding.chipCar;
        chipMoto = binding.chipMoto;
        chipElectric = binding.chipElectric;
        chipSpecial = binding.chipSpecial;
    }

    private void bindDonut(){
        dpvChart = binding.dpvChart;
        donutText = binding.donutText;
        reloadDonut();
    }
    private void reloadDonut(){
        MediatorLiveData<Pair<Integer[], Integer[]>> mediatorLiveData = new MediatorLiveData<>();

        LiveData<Integer[]> plazasTotalesLiveData = mainViewModel.getCantidadPlazas();
        LiveData<Integer[]> numPlazasOcupadasLiveData = mainViewModel.getCantidadPlazasOcupadas();

        mediatorLiveData.addSource(plazasTotalesLiveData, new Observer<Integer[]>() {
            @Override
            public void onChanged(Integer[] integers) {
                if (plazasTotalesLiveData.getValue() != null && numPlazasOcupadasLiveData.getValue() != null) {
                    mediatorLiveData.setValue(new Pair<>(plazasTotalesLiveData.getValue(), numPlazasOcupadasLiveData.getValue()));
                }
            }
        });

        mediatorLiveData.addSource(numPlazasOcupadasLiveData, new Observer<Integer[]>() {
            @Override
            public void onChanged(Integer[] integers) {
                if (plazasTotalesLiveData.getValue() != null && numPlazasOcupadasLiveData.getValue() != null) {
                    mediatorLiveData.setValue(new Pair<>(plazasTotalesLiveData.getValue(), numPlazasOcupadasLiveData.getValue()));
                }
            }
        });

        mediatorLiveData.observe(getViewLifecycleOwner(), new Observer<Pair<Integer[], Integer[]>>() {
            @Override
            public void onChanged(Pair<Integer[], Integer[]> pair) {
                Integer[] plazasTotales = pair.first;
                Integer[] numPlazasOcupadas = pair.second;
                Integer[] plazasLibres = new Integer[]{plazasTotales[0] - numPlazasOcupadas[0],
                        plazasTotales[1] - numPlazasOcupadas[1],
                        plazasTotales[2] - numPlazasOcupadas[2],
                        plazasTotales[3] - numPlazasOcupadas[3]};
                int sumaPlazasOcupadasTotales = numPlazasOcupadas[0] + numPlazasOcupadas[1] + numPlazasOcupadas[2] + numPlazasOcupadas[3];
                int sumaPlazasTotales = plazasTotales[0] + plazasTotales[1] + plazasTotales[2] + plazasTotales[3];
                int sumaPlazasLibresTotales = plazasLibres[0] + plazasLibres[1] + plazasLibres[2] + plazasLibres[3];

                float porcentajeCoche = (float) plazasLibres[0] / sumaPlazasLibresTotales * 100;
                float porcentajeMoto = (float) plazasLibres[1] / sumaPlazasLibresTotales * 100;
                float porcentajeElectrico = (float) plazasLibres[2] / sumaPlazasLibresTotales * 100;
                float porcentajeEspecial = (float) plazasLibres[3] / sumaPlazasLibresTotales * 100;

                chipCar.setText(String.valueOf(plazasLibres[0]));
                chipMoto.setText(String.valueOf(plazasLibres[1]));
                chipElectric.setText(String.valueOf(plazasLibres[2]));
                chipSpecial.setText(String.valueOf(plazasLibres[3]));

                donutText.setText(String.format("%s / %s", sumaPlazasLibresTotales, sumaPlazasTotales));

                DonutSection cocheSection = new DonutSection("Coche", ContextCompat.getColor(getContext(),
                        R.color.donut_car), porcentajeCoche);
                DonutSection motoSection = new DonutSection("Moto", ContextCompat.getColor(getContext(),
                        R.color.donut_electric), porcentajeMoto);
                DonutSection electricoSection = new DonutSection("Eléctrico", ContextCompat.getColor(getContext(),
                        R.color.donut_moto), porcentajeElectrico);
                DonutSection especialSection = new DonutSection("Especial", ContextCompat.getColor(getContext(),
                        R.color.donut_special), porcentajeEspecial);

                dpvChart.setStrokeCap(DonutStrokeCap.BUTT);
                dpvChart.setCap(porcentajeCoche + porcentajeMoto + porcentajeElectrico + porcentajeEspecial);
                dpvChart.submitData(new ArrayList<>(Arrays.asList(cocheSection, motoSection, electricoSection, especialSection)));
                progressBar.setVisibility(View.GONE);

            }
        });

    }
    private void bindDonutProgressBar(){
        mainViewModel.getBookingModified().observe(getViewLifecycleOwner(), modifiedReservation -> {
            reloadDonut();
            Lifecycle.State life = getViewLifecycleOwner().getLifecycle().getCurrentState();
            //si se accede después desde un cambio de vista que no se cargue el progressbar
            if(life.equals(Lifecycle.State.RESUMED)){
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    public void bindAddButton(){
        binding.addButton.setOnClickListener(v -> {
            mainViewModel.setShouldNavigateTooBookingFragment(new Pair<>(2, false));
        });
    }
    public void bindViewMoreButton(){
        binding.viewMoreButton.setOnClickListener(v -> {
            mainViewModel.setShouldNavigateTooBookingFragment(new Pair<>(1, false));
        });
    }
}
