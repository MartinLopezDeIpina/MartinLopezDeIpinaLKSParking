package com.lksnext.parking.data.callbacks;

import com.lksnext.parking.data.RegisterErrorType;

public interface RegisterCallback {
    void onSuccess();
    void onRegisterFailure(RegisterErrorType error);
}
