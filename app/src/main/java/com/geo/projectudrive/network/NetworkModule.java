package com.geo.projectudrive.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geo.projectudrive.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class NetworkModule {

    private static final String TAG_NETWORK = "Network tag";
    private static NetworkModule sNetworkModule;
    private OkHttpClient sOkHttpClient;
    private Retrofit sRetrofitHandle;


    private NetworkModule(Context context) {
        getOkHttpClient(context);
        getRetrofit();
    }

    public static NetworkModule getInstance(Context context) {
        if (sNetworkModule == null) {
            sNetworkModule = new NetworkModule(context);
        }

        return sNetworkModule;
    }

    /**
     * Setting Cache file
     *
     * @return cache
     */
    private static Cache provideCache(Context context) {
        Cache cache = null;
        try {
            cache = new Cache(new File(context.getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e("Cache", "Could not create Cache!", e);
        }
        return cache;
    }

    private void getOkHttpClient(Context context) {
        if (sOkHttpClient == null) {
            OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
            sOkHttpClient = okHttpClientBuilder
                    .addInterceptor(new RequestInterceptor(context))
                    .readTimeout(3,
                            TimeUnit.MINUTES)
                    .connectTimeout(3,
                            TimeUnit.MINUTES)
                    .cache(provideCache(context))
                    .build();
        }
    }

    private void getRetrofit() {
        if (sRetrofitHandle == null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);

            sRetrofitHandle = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(sOkHttpClient)
                    .addConverterFactory(JacksonConverterFactory.create(mapper))
                    .build();
        }
    }

    public Retrofit getApiImplementer() {
        return sRetrofitHandle;
    }

    private static class RequestInterceptor implements Interceptor {
        private final Context mContext;

        RequestInterceptor(Context context) {
            mContext = context;
        }

        @Override
        public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {

            try {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder();

                Request newRequest = requestBuilder.build();
                Response networkResponse = chain.proceed(newRequest);
                Log.e("Request", newRequest.toString());

                assert networkResponse.body() != null;
                return networkResponse.newBuilder()
                        .body(networkResponse.body())
                        .build();


            } catch (Exception ex) {
                Log.e(TAG_NETWORK, ex.toString());
                throw ex;
            }
        }
    }
}
