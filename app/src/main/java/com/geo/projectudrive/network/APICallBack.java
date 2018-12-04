package com.geo.projectudrive.network;

import android.support.annotation.NonNull;

import com.geo.projectudrive.model.response.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class APICallBack<T> implements Callback<T> {
    private static final String API_SUCCESS_CODE = "200";
    private static final String UNAUTHORISED_CODE = "403";
    private static final int UNATUHORISED = 403;
    private static final String INTERNAL_SERVER_ERROR = "500";
    private static final String UNKNOWN_ERROR = "1001";

    protected abstract void onSuccessResponse(Call<T> call, T response);

    protected abstract void onFailureResponse(Call<T> call, String errorCode);

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            T baseResponse = response.body();
            if (baseResponse != null && baseResponse instanceof BaseResponse) {
                BaseResponse baseResponseObject = (BaseResponse) baseResponse;
                if (baseResponseObject.isSuccess()) {
                    onSuccessResponse(call, baseResponse);
                } else {
                    handleErrorCase(call);
                }
            }
        } else {
            switch (response.code()) {
                case UNATUHORISED:
                    onFailureResponse(call, UNAUTHORISED_CODE);
                    break;
                default:
                    onFailureResponse(call, UNKNOWN_ERROR);
            }
        }
    }

    private void handleErrorCase(Call<T> call) {
        onFailureResponse(call, INTERNAL_SERVER_ERROR);
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onFailureResponse(call, UNKNOWN_ERROR);
    }
}
