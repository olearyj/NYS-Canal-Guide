# NYS Canal Guide - Native Android Application

This Android app is published in the Google Play Store: [NYS Canal Guide](https://play.google.com/store/apps/details?id=com.AYC.canalguide&hl=en&gl=US)

### About
This app connects to 21 different endpoints maintained by the NYS Canal Corporation. Since users will likely be using the app while traveling through poor cell service areas, all data will be cached for offline use. Since the data isn't updated very frequently the app will compare server side last modified dates to app side dates before syncing to operate efficiency.

This app will help users find points of interest along rivers throughout the New York state canal systems. Points of interest include locks, marinas and public docks, boat launches, bridges and guarded gates, cruises and rentals. You can then find out information such as a phone number, web site, and other information about the site. Now the map includes navigation information such as buoys, beacons and lighthouses.

### Running this project
Be sure to add your own Google API key to the `values/google_maps_api.xml` file. To create an api key, follow these [instructions](https://developers.google.com/maps/documentation/android-sdk/get-api-key).


## Technology stack / libraries / design patterns / frameworks

- **Dependency injection** - [HILT](https://developer.android.com/training/dependency-injection/hilt-android) (Build on top of Dagger)
- **Network** - Retrofit2
- **Concurrency** - Coroutines
- **Data storage** - Room, Preference library, SharedPreferences
- **Design patterns** - MVVM, Repository, Observable, Factory, Singleton
- **Legacy API support** & backwards compatibility - AndroidX
- **Navigation component architecture**
- **View Binding & Data Binding**
- **LiveData**
- **Firebase Analytics**
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
 - [ ] Replace LiveData with Flow
 - [ ] Remove data binding and / or use Compose
 - [ ] Use Gradle version catalog
 - [ ] Add attributions for open source libraries used with: https://developers.google.com/android/guides/opensource
 - [ ] Consider adding ktor client
 - [ ] Replace SharedPreferences with Jetpack DataStore
 - [ ] Upgrade libraries
 - [ ] Add analytics


## History


- 06/30/2021 - Released **v3.4**
	- Performance enhancements
	- Dependency updates (HILT and others)
	- Migrated from Kotlin Synthetics to ViewBinding
	- Bug fix: In fullscreen, map wasn't displaying all the way to the top of the screen if there was a display cutout (camera)
- 11/30/2020 - Released **v3.1**
	- Full Kotlin re-write for enhanced user experience
	- Performance optimizations
	- User interface upgrades
	- New features including full screen mode available in settings
- 09/23/2019 - Released **v2.7**
- ...
- 08/29/2014 - Released **v1.0**
