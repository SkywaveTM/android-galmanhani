package me.skywave.helloapplication.utils.server_api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public class ServerApi {
    private GalmanhaniService service;

    public ServerApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://galmanhani.appspot.com/_ah/api/galmanhani/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(GalmanhaniService.class);
    }

    public void addHostDensityData(String hostId, int connectionCount, int density) {
        if (density < 0) {
            return;
        }

        HostDensityData data = new HostDensityData(hostId, connectionCount, density);

        try {
            service.addHostDensityData(data).execute();
        } catch (IOException ignored) {

        }
    }

    public void addHostConnectionData(String hostId, int connectionCount) {
        HostDensityData data = new HostDensityData(hostId, connectionCount, 0);

        try {
            service.addHostConnectionData(data).execute();
        } catch (IOException ignored) {

        }
    }

    public List<HostDensityData> listHostDensityData(String hostId) {
        try {
            Response<Result> response = service.listHostDensityData(hostId).execute();
            Result result = response.body();

            if (result.items == null) {
                return new ArrayList<>();
            }

            return result.items;
        } catch (IOException e) {
            return null;
        }
    }

    public HostDensityData getHostConnectionData(String hostId) {
        try {
            List<HostDensityData> temp = service.getHostDensityData(hostId).execute().body().items;
            if (temp != null && temp.size() > 0) {
                return temp.get(0);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private interface GalmanhaniService {
        @GET("host_density")
        Call<Result> listHostDensityData(@Query("host_id") String hostId);

        @POST("host_density")
        Call<Void> addHostDensityData(@Body HostDensityData hostData);

        @GET("host_connection")
        Call<Result> getHostDensityData(@Query("host_id") String hostId);

        @POST("host_connection")
        Call<Void> addHostConnectionData(@Body HostDensityData hostData);
    }

    private class Result {
        String kind;
        String etag;
        List<HostDensityData> items;
    }
}
