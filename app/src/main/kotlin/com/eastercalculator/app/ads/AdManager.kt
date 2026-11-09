package com.eastercalculator.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AdManager {
    private const val TAG = "AdManager"

    // Official AdMob Ad Unit IDs
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3207706111232143/1372525401"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3207706111232143/4510979584"

    private var rewardedAd: RewardedAd? = null
    private var isAdLoading = false

    private val _isRewardedReady = MutableStateFlow(false)
    val isRewardedReady: StateFlow<Boolean> = _isRewardedReady.asStateFlow()

    fun initialize(context: Context) {
        try {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "AdMob başlatıldı: $status")
                loadRewardedAd(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob başlatma hatası", e)
        }
    }

    fun loadRewardedAd(context: Context) {
        if (rewardedAd != null || isAdLoading) return
        isAdLoading = true

        val request = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            request,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Ödüllü reklam yüklendi.")
                    rewardedAd = ad
                    isAdLoading = false
                    _isRewardedReady.value = true
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Ödüllü reklam yüklenemedi: ${loadAdError.message}")
                    rewardedAd = null
                    isAdLoading = false
                    _isRewardedReady.value = false
                }
            }
        )
    }

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdDismissed: (() -> Unit)? = null
    ) {
        val ad = rewardedAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ödüllü reklam kapatıldı.")
                    rewardedAd = null
                    _isRewardedReady.value = false
                    onAdDismissed?.invoke()
                    loadRewardedAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Ödüllü reklam gösterilemedi: ${adError.message}")
                    rewardedAd = null
                    _isRewardedReady.value = false
                    onAdDismissed?.invoke()
                    loadRewardedAd(activity)
                }
            }

            ad.show(activity) { rewardItem ->
                Log.d(TAG, "Kullanıcı ödülü kazandı: ${rewardItem.type} - ${rewardItem.amount}")
                onRewardEarned()
            }
        } else {
            Log.w(TAG, "Ödüllü reklam hazır değildi, alternatif ödül tetikleniyor.")
            // Geliştirme ortamında veya ağ yokken kullanıcı mağdur edilmez
            onRewardEarned()
            loadRewardedAd(activity)
        }
    }
}
