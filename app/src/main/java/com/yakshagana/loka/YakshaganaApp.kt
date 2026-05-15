package com.yakshagana.loka

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.yakshagana.loka.notifications.NotificationChannels
import okhttp3.OkHttpClient

class YakshaganaApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.create(this)
    }

    override fun newImageLoader(): ImageLoader {
        val userAgent =
            "YakshaganaLoka/1.0 (Android ${Build.VERSION.RELEASE}; ${Build.MANUFACTURER} ${Build.MODEL})"
        val okHttp = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
                chain.proceed(req)
            }
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(okHttp)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .build()
            }
            .build()
    }
}
