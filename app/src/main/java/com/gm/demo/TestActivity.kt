package com.gm.demo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.baidu.mobads.sdk.api.RequestParameters
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.CSJAdError
import com.bytedance.sdk.openadsdk.CSJSplashAd
import com.bytedance.sdk.openadsdk.TTAdConfig
import com.bytedance.sdk.openadsdk.TTAdConstant
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdNative.CSJSplashAdListener
import com.bytedance.sdk.openadsdk.TTAdNative.NativeExpressAdListener
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTDislikeDialogAbstract
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.bytedance.sdk.openadsdk.TTNativeExpressAd.ExpressAdInteractionListener
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.bytedance.sdk.openadsdk.mediation.MediationConstant
import com.bytedance.sdk.openadsdk.mediation.ad.MediationAdSlot
import com.bytedance.sdk.openadsdk.mediation.manager.MediationBaseManager
import com.qq.e.ads.cfg.VideoOption

class TestActivity : Activity() {
    private val permissionList = ArrayList<String>()
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    private val tag = "GroMoreSdk"
    private val appId = ""//todo 填写appid
    private val splashAdPos = ""//todo 填写开屏广告位
    private val interstitialAdPos = ""//todo 填写插屏广告位
    private val nativeAdPos = ""//todo 填写信息流广告位
    private val bannerAdPos = ""//todo 填写banner广告位
    private val rewardVideoAdPos = ""//todo 填写激励视频广告位

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        //todo 检查权限（可选）
        for (permission in permissions) {
            val checkSelfPermission = checkSelfPermission(permission)
            if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
                continue
            }
            permissionList.add(permission)
        }
        if (!permissionList.isEmpty()) {
            requestPermissions(permissionList.toTypedArray<String?>(), 1000)
        }
        initListener()
    }

    private fun initListener() {
        //todo 初始化sdk
        findViewById<View>(R.id.btn_init).setOnClickListener {
            if(appId.isEmpty()){
                Toast.makeText(this, "请先填写appid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            initSdk(this)
        }

        //todo 加载和显示开屏广告
        findViewById<View>(R.id.btn_splash).setOnClickListener {
            loadSplashAd()
        }

        //todo 加载和显示激励视频
        findViewById<View>(R.id.btn_reward).setOnClickListener {
            loadRewardVideoAd()
        }

        //todo 加载和显示插屏广告
        findViewById<View>(R.id.btn_interstitial).setOnClickListener {
            loadInterstitialAd()
        }

        //todo 加载和显示信息流广告
        findViewById<View>(R.id.btn_native).setOnClickListener {
            loadNativeAd()
        }

        //todo 加载和显示Banner广告
        findViewById<View>(R.id.btn_banner).setOnClickListener {
            loadBannerAd()
        }
    }

    /**
     * 初始化SDK
     */
    private fun initSdk(context: Context) {
        TTAdSdk.init(context, TTAdConfig.Builder().appId(appId).useMediation(true).build())
        TTAdSdk.start(object : TTAdSdk.Callback {
            override fun success() {
                Log.i(tag, "初始化-成功")
                Toast.makeText(context, "初始化-成功", Toast.LENGTH_SHORT).show()
            }

            override fun fail(code: Int, msg: String?) {
                Log.i(tag, "初始化-失败，msg = $msg")
                Toast.makeText(context, "初始化-失败，msg = $msg", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 加载开屏广告
     */
    private fun loadSplashAd() {
        if (!TTAdSdk.isSdkReady()) {
            Toast.makeText(this, "请先初始化SDK", Toast.LENGTH_SHORT).show()
            return
        }
        val dm = resources.displayMetrics
        val width = dm.widthPixels
        val height = dm.heightPixels
        val ttAdNative = TTAdSdk.getAdManager().createAdNative(this)
        val adSlot = AdSlot.Builder()
            .setCodeId(splashAdPos)
            .setImageAcceptedSize(width, height)
            .build()
        ttAdNative.loadSplashAd(adSlot, object : CSJSplashAdListener {
            override fun onSplashLoadSuccess(csjSplashAd: CSJSplashAd?) {
            }

            override fun onSplashLoadFail(csjAdError: CSJAdError?) {
            }

            override fun onSplashRenderSuccess(csjSplashAd: CSJSplashAd?) {
                Log.i(tag, "开屏-加载成功")
                showSplashAd(csjSplashAd)
            }

            override fun onSplashRenderFail(csjSplashAd: CSJSplashAd?, csjAdError: CSJAdError) {
                Log.i(tag, "开屏-加载失败，error code = " + csjAdError.code + ", msg = " + csjAdError.msg)
            }
        }, 5000)
    }

    /**
     * 展示开屏广告
     */
    private fun showSplashAd(csjSplashAd: CSJSplashAd?) {
        csjSplashAd?.let {
            val splashView = it.splashView
            it.setSplashAdListener(object : CSJSplashAd.SplashAdListener {
                override fun onSplashAdShow(csjSplashAd: CSJSplashAd) {
                    Log.i(tag, "开屏-显示")
                    showShowEcpm(csjSplashAd.mediationManager)
                }

                override fun onSplashAdClick(csjSplashAd: CSJSplashAd?) {
                    Log.i(tag, "开屏-点击")
                }

                override fun onSplashAdClose(csjSplashAd: CSJSplashAd?, i: Int) {
                    Log.i(tag, "开屏-关闭")
                    splashView.parent?.let {  parent->
                        if (parent is ViewGroup) {
                            parent.removeView(splashView)
                        }
                    }
                }
            })
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            viewGroup.addView(splashView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    /**
     * 加载激励视频广告
     */
    private fun loadRewardVideoAd() {
        if (!TTAdSdk.isSdkReady()) {
            Toast.makeText(this, "请先初始化SDK", Toast.LENGTH_SHORT).show()
            return
        }
        val extraInfo = "额外信息"
        val adSlot = AdSlot.Builder()
            .setCodeId(rewardVideoAdPos)
            .setOrientation(TTAdConstant.VERTICAL)
            .setUserID("用户id")
            .setMediationAdSlot(
                MediationAdSlot.Builder()
                    .setBidNotify(true)
                    .setExtraObject(MediationConstant.ADN_PANGLE, extraInfo) //服务端奖励验证透传参数
                    .setExtraObject(MediationConstant.ADN_GDT, extraInfo)
                    .setExtraObject(MediationConstant.ADN_KS, extraInfo)
                    .setExtraObject(MediationConstant.ADN_BAIDU, extraInfo)
                    .setExtraObject(MediationConstant.ADN_SIGMOB, extraInfo)
                    .setExtraObject(MediationConstant.TT_GDT_NATIVE_VIEW_TAG, extraInfo)
                    .setExtraObject(MediationConstant.TT_GDT_NATIVE_LOGO_VIEW_TAG, extraInfo)
                    .setExtraObject(MediationConstant.TT_GDT_NATIVE_LOGO_VIEW_TAG, extraInfo)
                    .setExtraObject(MediationConstant.CUSTOM_DATA_KEY_GROMORE_EXTRA, extraInfo)
                    .build()
            )
            .build()
        TTAdSdk.getAdManager().createAdNative(this).loadRewardVideoAd(adSlot, object : TTAdNative.RewardVideoAdListener {
            override fun onError(i: Int, s: String?) {
                Log.i(tag, "激励视频-加载失败 code = $i, msg = $s")
            }

            override fun onRewardVideoAdLoad(ttRewardVideoAd: TTRewardVideoAd?) {
                Log.i(tag, "激励视频-加载成功")
                showRewardVideoAd(ttRewardVideoAd)
            }

            @Deprecated("Deprecated in Java")
            override fun onRewardVideoCached() {
            }

            override fun onRewardVideoCached(ttRewardVideoAd: TTRewardVideoAd?) {
                Log.i(tag, "激励视频-缓存成功")
            }
        })
    }

    /**
     * 展示激励视频
     */
    private fun showRewardVideoAd(ttRewardVideoAd: TTRewardVideoAd?) {
        ttRewardVideoAd?.let {
            it.setRewardAdInteractionListener(object : TTRewardVideoAd.RewardAdInteractionListener {
                override fun onAdShow() {
                    Log.i(tag, "激励视频-展示")
                    showShowEcpm(it.mediationManager)
                }

                override fun onAdVideoBarClick() {
                    Log.i(tag, "激励视频-点击")
                }

                override fun onAdClose() {
                    Log.i(tag, "激励视频-关闭")
                }

                override fun onVideoComplete() {
                    Log.i(tag, "激励视频-播放完成")
                }

                override fun onVideoError() {
                    Log.i(tag, "激励视频-播放错误")
                }

                @Deprecated("Deprecated in Java")
                override fun onRewardVerify(b: Boolean, i: Int, s: String?, i1: Int, s1: String?) {
                    Log.i(tag, "激励视频-奖励认证：$b")
                }

                override fun onRewardArrived(b: Boolean, i: Int, bundle: Bundle?) {
                    //todo 在这里可以处理奖励
                    Log.i(tag, "激励视频-奖励到达：$b")
                }

                override fun onSkippedVideo() {
                    Log.i(tag, "激励视频-跳过视频")
                }
            })
            it.showRewardVideoAd(this)
        }

    }

    /**
     * 加载插屏广告
     */
    private fun loadInterstitialAd() {
        if (!TTAdSdk.isSdkReady()) {
            Toast.makeText(this, "请先初始化SDK", Toast.LENGTH_SHORT).show()
            return
        }
        val adSlot = AdSlot.Builder()
            .setCodeId(interstitialAdPos)
            .setOrientation(TTAdConstant.VERTICAL)
            .setMediationAdSlot(
                MediationAdSlot.Builder()
                    .setMuted(true) //是否静音
                    .setVolume(0.7f) //设置音量
                    .setBidNotify(true) //竞价结果通知
                    .build()
            )
            .build()
        TTAdSdk.getAdManager().createAdNative(this).loadFullScreenVideoAd(adSlot, object : TTAdNative.FullScreenVideoAdListener {
            override fun onError(i: Int, s: String?) {
                Log.i(tag, "插屏-加载失败 code = $i , msg = $s")
            }

            override fun onFullScreenVideoAdLoad(ttFullScreenVideoAd: TTFullScreenVideoAd?) {
                Log.i(tag, "插屏-加载成功")
                showInterstitialAd(ttFullScreenVideoAd)
            }

            @Deprecated("Deprecated in Java")
            override fun onFullScreenVideoCached() {
            }

            override fun onFullScreenVideoCached(ttFullScreenVideoAd: TTFullScreenVideoAd?) {
            }
        })
    }

    /**
     * 展示插屏广告
     */
    private fun showInterstitialAd(ttFullScreenVideoAd: TTFullScreenVideoAd?) {
        ttFullScreenVideoAd?.let {
            it.setFullScreenVideoAdInteractionListener(object : TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
                override fun onAdShow() {
                    Log.i(tag, "插屏-显示")
                    showShowEcpm(it.mediationManager)
                }

                override fun onAdVideoBarClick() {
                    Log.i(tag, "插屏-点击")
                }

                override fun onAdClose() {
                    Log.i(tag, "插屏-关闭")
                }

                override fun onVideoComplete() {
                    Log.i(tag, "插屏-播放完成")
                }

                override fun onSkippedVideo() {
                    Log.i(tag, "插屏-跳过")
                }
            })
            it.showFullScreenVideoAd(this)
        }
    }

    /**
     * 加载信息流广告
     */
    private fun loadNativeAd() {
        if (!TTAdSdk.isSdkReady()) {
            Toast.makeText(this, "请先初始化SDK", Toast.LENGTH_SHORT).show()
            return
        }
        val nativeWidth = resources.displayMetrics.widthPixels
        val adSlot = AdSlot.Builder()
            .setCodeId(nativeAdPos) //todo 广告位id
            .setAdCount(1) //todo 一次拉取广告数量
            .setImageAcceptedSize(nativeWidth, 0) //todo 设置宽高，单位是dp
            .setMediationAdSlot(
                MediationAdSlot.Builder()
                    .setExtraObject(
                        MediationConstant.KEY_GDT_VIDEO_OPTION, VideoOption.Builder()
                            .setAutoPlayMuted(true) //todo gdt是否静音
                            .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS)
                            .build()
                    )
                    .setExtraObject(
                        MediationConstant.KEY_BAIDU_REQUEST_PARAMETERS, RequestParameters.Builder()
                            .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ALWAYS)
                            .build()
                    )
                    .build()
            )
            .build()
        TTAdSdk.getAdManager().createAdNative(this).loadNativeExpressAd(adSlot, object : NativeExpressAdListener {
            override fun onError(i: Int, s: String?) {
                Log.i(tag, "信息流-加载失败: code = $i, msg = $s")
            }

            override fun onNativeExpressAdLoad(list: MutableList<TTNativeExpressAd>?) {
                if (!list.isNullOrEmpty()) {
                    Log.i(tag, "信息流-加载成功")
                    val ttNativeExpressAd = list[0]
                    showNativeBannerAd(1,ttNativeExpressAd)
                } else {
                    Log.i(tag, "信息流-加载失败:广告为空")
                }
            }
        })
    }

    /**
     * 加载横幅广告
     */
    private fun loadBannerAd() {
        if (!TTAdSdk.isSdkReady()) {
            Toast.makeText(this, "请先初始化SDK", Toast.LENGTH_SHORT).show()
            return
        }
        val adSlot = AdSlot.Builder()
            .setCodeId(bannerAdPos) //todo 广告位id
            .setAdCount(1) //todo 一次拉取广告数量
            .setExpressViewAcceptedSize(300f, 0f) //todo 设置宽高，单位是dp
            .build()
        TTAdSdk.getAdManager().createAdNative(this).loadBannerExpressAd(adSlot, object : NativeExpressAdListener {
            override fun onError(i: Int, s: String?) {
                Log.i(tag, "横幅-加载失败: code = $i, msg = $s")
            }

            override fun onNativeExpressAdLoad(list: MutableList<TTNativeExpressAd>?) {
                if (!list.isNullOrEmpty()) {
                    Log.i(tag, "横幅-加载成功")
                    val ttNativeExpressAd = list[0]
                    showNativeBannerAd(2,ttNativeExpressAd)
                } else {
                    Log.i(tag, "横幅-加载失败:广告为空")
                }
            }
        })
    }

    /**
     * 展示横幅广告
     */
    private fun showNativeBannerAd(type: Int, ttNativeExpressAd: TTNativeExpressAd) {
        val name = if(type == 1) "信息流" else "横幅"
        ttNativeExpressAd.setExpressInteractionListener(object : ExpressAdInteractionListener {
            override fun onAdClicked(view: View?, i: Int) {
                Log.i(tag, "$name-点击")
            }

            override fun onAdShow(view: View?, i: Int) {
                Log.i(tag, "$name-展示")
                showShowEcpm(ttNativeExpressAd.mediationManager)
            }

            override fun onRenderFail(view: View?, s: String?, i: Int) {
                Log.i(tag, "$name-渲染失败")
            }

            override fun onRenderSuccess(view: View?, v: Float, v1: Float) {
                Log.i(tag, "$name-渲染成功")
            }
        })
        val expressAdView = ttNativeExpressAd.expressAdView
        if (expressAdView != null) {
            val viewGroup = findViewById<ViewGroup>(R.id.layout_ad_container)
            viewGroup.removeAllViews()
            viewGroup.addView(expressAdView)
            val dislikeDialog = ttNativeExpressAd.getDislikeDialog(this)
            dislikeDialog.setDislikeInteractionCallback(object :  TTAdDislike.DislikeInteractionCallback {
                override fun onShow() {
                    dislikeDialog.resetDislikeStatus()
                    //如果需要删除view，可以在这里调用（可选）
                    viewGroup.removeView(expressAdView)
                }

                override fun onSelected(p0: Int, p1: String?, p2: Boolean) {
                    
                }

                override fun onCancel() {
                    
                }

            })
        }
        ttNativeExpressAd.render()
    }

    private fun showShowEcpm(manager: MediationBaseManager) {
        val info = manager.showEcpm
        Log.i(tag, "价格（真实的价格是需要除以100）:" + info.ecpm)
        Log.i(tag, "代码位:" + info.slotId)
        Log.i(tag, "广告平台:" + info.sdkName)
    }
}
