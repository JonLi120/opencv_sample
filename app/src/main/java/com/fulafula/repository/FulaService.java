package com.fulafula.repository;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;
import retrofit2.http.POST;

//TODO
public interface FulaService {
//    Template
    @POST()
    Single<Response> mockApi();
}
