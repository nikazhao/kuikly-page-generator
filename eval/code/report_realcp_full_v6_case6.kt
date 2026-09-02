package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

class AudioHapticsModule : Module() {
    override fun moduleName(): String = "KRAudioHapticsModule"

    companion object {
        const val MODULE_NAME = "KRAudioHapticsModule"
    }

    fun playSound(soundName: String) {
        asyncToNativeMethod(
            "playSound",
            JSONObject().apply { put("soundName", soundName) },
            null
        )
    }

    fun vibrate(durationMs: Int) {
        asyncToNativeMethod(
            "vibrate",
            JSONObject().apply { put("durationMs", durationMs) },
            null
        )
    }
}

@Page("RegisterPage")
internal class RegisterPage : Pager() {

    private var phoneNumber by observable("")
    private var verificationCode by observable("")
    private var password by observable("")
    private var countdown by observable(0)

    private lateinit var spModule: SharedPreferencesModule
    private lateinit var networkModule: NetworkModule
    private lateinit var audioModule: AudioHapticsModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        networkModule = acquireModule(NetworkModule.MODULE_NAME)
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
        phoneNumber = spModule.getString("register_phone")
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                }
                View {
                    attr {
                        height(pagerData.safeAreaInsets.top)
                    }
                }
                View {
                    attr {
                        flex(1f)
                        flexDirectionColumn()
                        alignItemsCenter()
                        padding(16f, 16f, 16f, 16f)
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(48f)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                            margin(12f, 12f, 12f, 12f)
                            keyboardTypeNumber()
                        }
                        event {
                            textDidChange { params ->
                                ctx.phoneNumber = params.text
                            }
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            flexDirectionRow()
                            marginTop(16f)
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(48f)
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                                margin(12f, 12f, 12f, 12f)
                                keyboardTypeNumber()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.verificationCode = params.text
                                }
                            }
                        }
                        Button {
                            attr {
                                width(120f)
                                height(48f)
                                borderRadius(8f)
                                backgroundColor(0xFF07C160)
                                marginLeft(12f)
                                titleAttr {
                                    text("获取验证码")
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    if (ctx.phoneNumber.isEmpty()) {
                                        return@touchDown
                                    }
                                    ctx.countdown = 60
                                    ctx.networkModule.requestPost(
                                        "https://api.example.com/sendCode",
                                        JSONObject().apply {
                                            put("phone", ctx.phoneNumber)
                                        }
                                    ) { response, success, msg ->
                                    }
                                    ctx.spModule.setString("register_phone", ctx.phoneNumber)
                                }
                            }
                        }
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(48f)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                            margin(12f, 12f, 12f, 12f)
                            keyboardTypePassword()
                            marginTop(16f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.password = params.text
                            }
                        }
                    }
                    Button {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(48f)
                            borderRadius(8f)
                            backgroundColor(0xFF07C160)
                            marginTop(24f)
                            titleAttr {
                                text("注册")
                                color(Color.WHITE)
                                fontSize(16f)
                            }
                        }
                        event {
                            touchDown {
                                if (ctx.phoneNumber.isEmpty() || ctx.verificationCode.isEmpty() || ctx.password.isEmpty()) {
                                    return@touchDown
                                }
                                ctx.networkModule.requestPost(
                                    "https://api.example.com/register",
                                    JSONObject().apply {
                                        put("phone", ctx.phoneNumber)
                                        put("code", ctx.verificationCode)
                                        put("password", ctx.password)
                                    }
                                ) { response, success, msg ->
                                    ctx.audioModule.playSound("register_success")
                                }
                            }
                        }
                    }
                }
                View {
                    attr {
                        height(pagerData.safeAreaInsets.bottom)
                    }
                }
            }
        }
    }
}