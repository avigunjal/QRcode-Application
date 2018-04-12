package com.barcodeapp.ApiClient;

import com.barcodeapp.Model.CustomerDetails;
import com.barcodeapp.Model.CustomerInfo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by AVI on 11-04-2018.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("aaa_registration.php")
    Call<CustomerDetails> CUSTOMER_DETAILS_CALL(
            @Field("name") String name,
            @Field("email") String email,
            @Field("dob") String dob,
            @Field("mobile") String mobile
    );


    @GET("get_customer.php")
    Call<CustomerInfo> get_details(
            @Query("email") String email
    );

}
