package com.teal.a276.walkinggroup.proxy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * General support for getting the Retrofit proxy object.
 * Adds:
 *   - Logging
 *   - Setting API key header
 *   - Setting Authorization header (and managing token received)
 *
 * For more on Retrofit, see http://square.github.io/retrofit/
 */
public class ServerManager {
    private static final String SERVER_URL = "https://cmpt276-1177-bf.cmpt.sfu.ca:8443/";
    private static final String API_KEY = "BB23730A-C1B3-4B65-855E-C538EE143FDC";


    // Allow client-code to register callback for when the token is received.
    // NOTE: the current proxy does not upgrade to using the token!
    private static ServerResult<String> receivedTokenCallback;
    public static void setOnTokenReceiveCallback(ServerResult<String> callback) {
        receivedTokenCallback = callback;
    }

    /**
     * Return the proxy that client code can use to call server.
     * @param token    The token you have been issued
     * @return proxy object to call the server.
     */
    public static AbstractServerManager getProxy(String token) {
        // Enable Logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new AddHeaderInterceptor(API_KEY, token))
                .build();

        // Build Retrofit proxy object for server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(AbstractServerManager.class);
    }

    /**
     * Simplify the calling of the "Call"
     * - Handle error checking in one place and put up toast & log on failure.
     * - Callback to simplified interface on success.
     * @param caller    Call object returned by the proxy
     * @param callback  Client-code to execute when we have a good answer for them or when an error occured.
     * @param <T>       The type of data that Call object is expected to fetch
     */
    public static <T> void callProxy(Call<T> caller, @NonNull final ServerResult<T> callback) {
        caller.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull retrofit2.Response<T> response) {

                // Process the response
                if (response.errorBody() == null) {
                    // Check for authentication token:
                    String tokenInHeader = response.headers().get("Authorization");
                    if (tokenInHeader != null) {
                        if (receivedTokenCallback != null) {
                            receivedTokenCallback.result(tokenInHeader);
                        } else {
                            // We got the token, but nobody wanted it!
                            Log.w("ProxyBuilder", "WARNING: Received token but no callback registered for it!");
                        }
                    }
                    T body = response.body();
                    callback.result(body);

                } else {
                    String message;
                    try {
                        message = "CALL TO SERVER FAILED:\n" + response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                        message = "Unable to decode response (body or error's body).";
                    }

                    callback.error(message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                String message = "Server Error: " + t.getMessage();
                callback.error(message);
            }
        });
    }

    private static class AddHeaderInterceptor implements Interceptor {
        private String apiKey;
        private String token;

        public AddHeaderInterceptor(String apiKey, String token) {
            this.apiKey = apiKey;
            this.token = token;
        }

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder();
            // Add API header
            if (apiKey != null) {
                builder.header("apiKey", apiKey);
            }
            // Add Token
            if (token != null) {
                builder.header("Authorization", token);
            }
            Request modifiedRequest = builder.build();

            return chain.proceed(modifiedRequest);
        }
    }
}