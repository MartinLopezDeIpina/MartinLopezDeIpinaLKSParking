package com.lksnext.parking.data;

import com.lksnext.parking.domain.Plaza;

import java.util.List;

public interface PlazaCallback {
    void onCallback(List<Plaza> plazas);
}
