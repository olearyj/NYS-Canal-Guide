# NYS Canal Guide - Native Android Application

This Android app is published in the Google Play Store: [NYS Canal Guide](https://play.google.com/store/apps/details?id=com.AYC.canalguide&hl=en&gl=US)


### Running this project
Be sure to add your own Google API key to the `values/google_maps_api.xml` file. To get an api key, follow these [instructions](https://developers.google.com/maps/documentation/android-sdk/get-api-key).


## Technology stack / libraries / design patterns / frameworks

- **Dependency injection** - [HILT](https://developer.android.com/training/dependency-injection/hilt-android) (Build on top of Dagger)
- **Network** - Retrofit2
- **Concurrency** - Coroutines
- **Data storage** - Room, Preference library, SharedPreferences
- **Design patterns** - MVVM, Repository, Observable, Factory, Singleton
- **Legacy API support** & backwards compatibility - AndroidX
- **Navigation component architecture**
- **Data Binding**
- **LiveData**
- **UI/UX**
	- [Material design](https://material.io/)
	- [Container transformation pattern](https://material.io/design/motion/the-motion-system.html#container-transform)
	- Google Maps
	- Immerse mode / fullscreen map
- **Support in-app updates** - [Play Core library](https://developer.android.com/guide/playcore/in-app-updates)
- **XML parsing** - [TikXML](https://github.com/Tickaroo/tikxml)


## TO-DO items

 - [ ] Integrate calendar events [data](http://www.canals.ny.gov/xml/calendar.xml)
 - [ ] Integrate heritage sites and lodging [data](http://www.canals.ny.gov/developers/index.html)
 - [ ] [Google Places API](https://developers.google.com/places/web-service/overview) for map in details fragment (privacy policy required for API usage)
 - [ ] Dark mode to Material design specs
 - [ ] Add UI/Unit testing
 - [ ] Add Ads and in-app purchase for ad-free mode
 - [ ] Option to sponsor a marina and add their club's flag to the map


## History

- TBD - Released **v3.0** - Full Kotlin re-write
- 09/23/2019 - Released **v2.7**
- ...
- 08/29/2014 - Released **v1.0**
