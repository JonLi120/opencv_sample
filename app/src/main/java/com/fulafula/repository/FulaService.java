package com.fulafula.repository;

import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

//TODO
public interface FulaService {

//    @Streaming
    @GET
    Single<Response<ResponseBody>> getPdfFile(@Url String url);
}
