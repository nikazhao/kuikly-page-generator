package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Text
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

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var errorMessage by observable("")

    private lateinit var audioModule: AudioHapticsModule
    private lateinit var sp: SharedPreferencesModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)!!
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)!!
        username = sp.getString("saved_username")
        password = sp.getString("saved_password")
    }

    fun onLoginClick() {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage = "请输入用户名和密码"
            audioModule.playSound("error")
            audioModule.vibrate(100)
        } else {
            errorMessage = ""
            sp.setString("saved_username", username)
            sp.setString("saved_password", password)
            audioModule.playSound("login_success")
            audioModule.vibrate(50)
        }
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    alignItemsCenter()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Text {
                    attr {
                        text("登录")
                        fontSize(28f)
                        fontWeightBold()
                        color(Color(0xFF333333))
                        marginTop(80f)
                        marginBottom(40f)
                    }
                }
                View {
                    attr {
                        width(pagerData.pageViewWidth * 0.85f)
                        flexDirectionColumn()
                        alignItemsCenter()
                    }
                    Text {
                        attr {
                            text("用户名")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginLeft(4f)
                            marginBottom(8f)
                            alignSelfFlexStart()
                        }
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth * 0.85f)
                            height(48f)
                            backgroundColor(Color(0xFFFFFFFF))
                            borderRadius(8f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(16f)
                            color(Color(0xFF333333))
                            placeholder("请输入用户名")
                            placeholderColor(Color(0xFFCCCCCC))
                            marginBottom(20f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.username = params.text
                            }
                        }
                    }
                    Text {
                        attr {
                            text("密码")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginLeft(4f)
                            marginBottom(8f)
                            alignSelfFlexStart()
                        }
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth * 0.85f)
                            height(48f)
                            backgroundColor(Color(0xFFFFFFFF))
                            borderRadius(8f)
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(16f)
                            color(Color(0xFF333333))
                            placeholder("请输入密码")
                            placeholderColor(Color(0xFFCCCCCC))
                            keyboardTypePassword()
                            marginBottom(32f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.password = params.text
                            }
                        }
                    }
                }
                Button {
                    attr {
                        width(pagerData.pageViewWidth * 0.85f)
                        height(48f)
                        backgroundColor(Color(0xFF4A90D9))
                        borderRadius(24f)
                        text("登录")
                        color(Color(0xFFFFFFFF))
                        fontSize(18f)
                        fontWeightMedium()
                    }
                    event {
                        onClick {
                            ctx.onLoginClick()
                        }
                    }
                }
                Text {
                    attr {
                        text(ctx.errorMessage)
                        fontSize(14f)
                        color(Color(0xFFFF4444))
                        marginTop(16f)
                        textAlignCenter()
                    }
                    vif({ ctx.errorMessage.isNotEmpty() })
                }
            }
        }
    }
}