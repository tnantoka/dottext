package com.tnantoka.dottext.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.lifecycleScope
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdFormat
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkUtils
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.tnantoka.dottext.R
import com.tnantoka.dottext.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(R.layout.activity_main), MaxAdViewAdListener {
    private var adView: MaxAdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val info = AdvertisingIdClient.getAdvertisingIdInfo(this@MainActivity)
                    Log.d("dev", "id: ${info.id}")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        AppLovinSdk.getInstance(this).mediationProvider = "max"
        AppLovinSdk.getInstance(this).initializeSdk {
            createBannerAd()
        }
    }

    private fun createBannerAd() {
//        if (BuildConfig.DEBUG) {
//            return
//        }

        if (adView != null) {
            return
        }

        if (findViewById<FrameLayout>(R.id.detailFrame) == null) {
            adView = MaxAdView(BuildConfig.MAX_BANNER_UNIT_ID, this)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val heightPx = AppLovinSdkUtils.dpToPx(this, 50)
            adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx)
        } else {
            adView = MaxAdView(BuildConfig.MAX_MREC_UNIT_ID, MaxAdFormat.MREC, this)
            val widthPx = AppLovinSdkUtils.dpToPx(this, 300)
            val heightPx = AppLovinSdkUtils.dpToPx(this, 250)
            adView?.layoutParams = FrameLayout.LayoutParams(widthPx, heightPx)
        }
        adView?.setListener(this)
        adView?.setBackgroundColor(com.google.android.material.R.attr.colorOnPrimary)

        val containerLinear = findViewById<ViewGroup>(R.id.containerLinear)
        containerLinear.addView(adView)

        adView?.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd?) {
    }

    override fun onAdDisplayed(ad: MaxAd?) {
    }

    override fun onAdHidden(ad: MaxAd?) {
    }

    override fun onAdClicked(ad: MaxAd?) {
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
    }

    override fun onAdExpanded(ad: MaxAd?) {
    }

    override fun onAdCollapsed(ad: MaxAd?) {
    }
}
