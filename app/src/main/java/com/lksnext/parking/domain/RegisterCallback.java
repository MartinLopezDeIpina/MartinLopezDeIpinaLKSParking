package com.lksnext.parking.domain;

import com.lksnext.parking.data.RegisterErrorType;

public interface RegisterCallback {
    void onSuccess();
    void onRegisterFailure(RegisterErrorType error);
}
