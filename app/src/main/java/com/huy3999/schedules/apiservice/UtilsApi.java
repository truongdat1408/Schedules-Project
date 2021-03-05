package com.huy3999.schedules.apiservice;

public class UtilsApi {

    public static BaseApiService getAPIService(){
        return RetrofitClient.getClient().create(BaseApiService.class);
    }
}
