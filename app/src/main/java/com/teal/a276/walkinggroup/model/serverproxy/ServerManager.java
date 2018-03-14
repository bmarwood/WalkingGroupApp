package com.teal.a276.walkinggroup.model.serverproxy;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ServerManager {
    private static final String SERVER_URL = "https://cmpt276-1177-bf.cmpt.sfu.ca:8443/";
    private static final String API_KEY = "BB23730A-C1B3-4B65-855E-C538EE143FDC";
    private static String apiToken = null;


    /**
    * Return the proxy that client code can use to call server.
    * @return proxy object to call the server.
    */
    public static ServerProxy getServerRequest() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new AddHeaderInterceptor(API_KEY, apiToken))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(ServerProxy.class);
    }

    /**
     * Simplify the calling of the "Call"
     * - Handle error checking in one place and put up toast & log on failure.
     * - Callback to simplified interface on success.
     * @param caller    Call object returned by the proxy
     * @param resultCallback  Client-code to execute when the sever returns a result.
     * @param errorCallback   Client-code to execute when an error has occurred.
     * @param <T>       The type of data that Call object is expected to fetch
     */
    public static <T> void serverRequest(Call<T> caller, @NonNull final ServerResult<T> resultCallback, @NonNull final ServerError errorCallback) {
        caller.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull retrofit2.Response<T> response) {

                // Process the response
                //TODO: account creation DOESN'T return auth token
                if (response.errorBody() == null) {
                    // Check for authentication token:
                    String tokenInHeader = response.headers().get("Authorization");
                    if (tokenInHeader != null) {
                        apiToken = tokenInHeader;
                    }

                    T body = response.body();
                    if(body != null) {
                        Log.d("server response:", body.toString());
                    }
                    resultCallback.result(body);

                } else {
                    errorCallback.error(getError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                Log.e("Server Connection Error", Log.getStackTraceString(t));
                errorCallback.error("");
            }
        });
    }

    //idea from: https://stackoverflow.com/questions/32519618/retrofit-2-0-how-to-get-deserialised-error-response-body
    static private String getError(@NonNull retrofit2.Response response) {
        String message = "";
        try {
            String errorMessage = response.errorBody().string();
            Log.e("Server Error:", errorMessage);

            JSONObject jObjError = new JSONObject(errorMessage);
            message = jObjError.getString("message");
        } catch (IOException | JSONException e) {
            Log.e("Error decoding message", Log.getStackTraceString(e));
        }

        return message;
    }

    private static class AddHeaderInterceptor implements Interceptor {
        private String apiKey;
        private String token;

        private AddHeaderInterceptor(String apiKey, String token) {
            this.apiKey = apiKey;
            this.token = token;
        }

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            okhttp3.Request originalRequest = chain.request();

            okhttp3.Request.Builder builder = originalRequest.newBuilder();
            // Add API header
            if (apiKey != null) {
                builder.header("apiKey", apiKey);
            }
            // Add Token
            if (token != null) {
                builder.header("Authorization", token);
            }
            okhttp3.Request modifiedRequest = builder.build();

            return chain.proceed(modifiedRequest);
        }
    }
}

