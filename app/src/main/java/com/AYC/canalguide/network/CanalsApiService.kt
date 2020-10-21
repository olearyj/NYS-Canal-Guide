package com.AYC.canalguide.network

import com.AYC.canalguide.data.entities.BridgeGateMarker
import com.AYC.canalguide.data.xml_classes.GuardGates
import com.AYC.canalguide.data.xml_classes.LiftBridges
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface CanalsApiService {

    @GET("liftbridges.xml")
    suspend fun getLiftBridgesText(): ResponseBody
    
    @GET("liftbridges.xml")
    suspend fun getLiftBridges(): Response<LiftBridges>

    @GET("guardgates.xml")
    suspend fun getGuardGates(): Response<GuardGates>


    companion object {

        operator fun invoke(connectivityInterceptor: ConnectivityInterceptor): CanalsApiService {
//            val loggingInterceptor = HttpLoggingInterceptor().apply {
//                this.level = HttpLoggingInterceptor.Level.BODY
//            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(connectivityInterceptor)
                //.addInterceptor(loggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://www.canals.ny.gov/xml/")
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(
                    TikXmlConverterFactory.create(
                        TikXml.Builder()
                            // CRTL + F on this site for more info on exceptionOnUnreadXml: https://github.com/Tickaroo/tikxml/blob/master/docs/AnnotatingModelClasses.md
                            .exceptionOnUnreadXml(false)
                            .build()
                    )
                )
                .build()

            return retrofit.create(CanalsApiService::class.java)
        }
    }

}