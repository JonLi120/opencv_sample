package com.fulafula.repository.repo;

import com.fulafula.repository.FulaService;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;

/***
 * remove after
 */
@Singleton
public class SimpleRepository {

    private final FulaService service;

    @Inject
    SimpleRepository(FulaService service) {
        this.service = service;
    }

    public Single<Response<ResponseBody>> getPdfFile(String url) {
        return service.getPdfFile(url);
    }
}
