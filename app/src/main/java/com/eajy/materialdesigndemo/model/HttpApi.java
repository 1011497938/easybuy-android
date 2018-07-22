package com.eajy.materialdesigndemo.model;

import org.json.JSONObject;

import java.util.Map;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface HttpApi {
    @FormUrlEncoded
    @POST("{path}")
    Observable<JSONObject> post(@Path("path") String path, @FieldMap Map<String, String> map);
    @FormUrlEncoded
    @POST("{root}/{path}")
    Observable<JSONObject> post(@Path("root") String root, @Path("path") String path, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("{root}/{path}")
    Observable<JSONObject> post(@Path("root") String root, @Path("path") String path);
    @FormUrlEncoded
    @POST("{path}")
    Observable<JSONObject> post(@Path("path") String path);

    @GET("{path}")
    Observable<JSONObject> get(@Path("path") String path);

    @GET("{path}")
    Observable<JSONObject> get(@Path("path") String path, @QueryMap Map<String, String> map);


    @GET("{root}/{path}")
    Observable<JSONObject> get(@Path("root") String root, @Path("path") String path, @QueryMap Map<String, String> map);

    @GET("{root}/{path}")
    Observable<JSONObject> get(@Path("root") String root, @Path("path") String path);

}