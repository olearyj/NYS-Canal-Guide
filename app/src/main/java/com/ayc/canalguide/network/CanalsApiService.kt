package com.ayc.canalguide.network

import com.ayc.canalguide.data.Constants.BASE_URL
import com.ayc.canalguide.data.xml_classes.*
import com.ayc.canalguide.utils.HtmlEscapeStringConverter
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Path


interface CanalsApiService {

    // Lift bridges and guard gates
    @GET("liftbridges.xml")
    suspend fun getLiftBridgesText(): ResponseBody
    @GET("liftbridges.xml")
    suspend fun getLiftBridges(): Response<LiftBridges>
    @GET("guardgates.xml")
    suspend fun getGuardGates(): Response<GuardGates>

    // Locks
    @GET("locks.xml")
    suspend fun getLocks(): Response<Locks>


    // Rentals and cruises
    @GET("boatsforhire.xml")
    suspend fun getRentalsCruises(): Response<Cruises>

    // Boat launches
    @GET("canalwatertrail.xml")
    suspend fun getBoatLaunches(): Response<Launches>

    // Marinas
    @GET("marinas.xml")
    suspend fun getMarinas(): Response<Marinas>


    /**
     * Extra API's to consider using below
     */

    // Lodging
    @GET("lodging.xml")
    suspend fun getLodging(): Response<Any>

    // Heritage sites
    @GET("heritagesites.xml")
    suspend fun getHeritageSites(): Response<Any>

    // Calendar events
    @GET("calendar.xml")
    suspend fun getCalendarEvents(): Response<Any>


    /**
     * Navigation
     */

    @HEAD("navinfo-{region}.xml")
    suspend fun navInfoHead(
        @Path("region") region: String
    ): Response<Void>

    @GET("navinfo-{region}.xml")
    suspend fun getNavInfo(
        @Path("region") region: String
    ): Response<NavInfos>

//    @GET("navinfo-hudsonriver.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-champlain.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-fortedward.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-erieeastern.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-frankfortharbor.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-uticaharbor.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-oswego.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-eriecentral.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-onondagalake.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-cayugaseneca.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-cayugalake.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-senecalake.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-eriewestern.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-geneseeriver.xml")
//    suspend fun getGuardGates(): Response<GuardGates>
//    @GET("navinfo-ellicottcreek.xml")
//    suspend fun getGuardGates(): Response<GuardGates>


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
                .baseUrl(BASE_URL)
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(
                    TikXmlConverterFactory.create(
                        TikXml.Builder()
                                // CRTL + F on this site for more info on exceptionOnUnreadXml: https://github.com/Tickaroo/tikxml/blob/master/docs/AnnotatingModelClasses.md
                                .exceptionOnUnreadXml(false)
                                // Escape all special characters such as &quot; and &amp;
                                .addTypeConverter(String::class.java, HtmlEscapeStringConverter())
                                .build()
                    )
                )
                .build()

            return retrofit.create(CanalsApiService::class.java)
        }
    }

}