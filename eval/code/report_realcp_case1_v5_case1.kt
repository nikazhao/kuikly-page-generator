package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
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
    private var errorMsg by observable("")
    private var loginSuccess by observable(false)

    private lateinit var audioModule: AudioHapticsModule
    private lateinit var sp: SharedPreferencesModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)
        username = sp.getString("saved_username")
        password = sp.getString("saved_password")
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    alignItemsCenter()
                    backgroundColor(Color(0xFFFFFFFFL))
                }
                Text {
                    attr {
                        text("登录")
                        fontSize(28f)
                        fontWeightBold()
                        color(Color(0xFF333333L))
                        marginTop(60f)
                    }
                }
                Input {
                    attr {
                        placeholder("请输入用户名")
                        fontSize(16f)
                        color(Color(0xFF333333L))
                        backgroundColor(Color(0xFFF5F5F5L))
                        borderRadius(8f)
                        size(pagerData.pageViewWidth * 0.85f, 48f)
                        marginTop(40f)
                        marginLeft(16f)
                        marginRight(16f)
                    }
                    event {
                        textDidChange { params ->
                            ctx.username = params.text
                        }
                    }
                }
                Input {
                    attr {
                        placeholder("请输入密码")
                        fontSize(16f)
                        color(Color(0xFF333333L))
                        backgroundColor(Color(0xFFF5F5F5L))
                        borderRadius(8f)
                        size(pagerData.pageViewWidth * 0.85f, 48f)
                        marginTop(16f)
                        marginLeft(16f)
                        marginRight(16f)
                        keyboardTypePassword()
                    }
                    event {
                        textDidChange { params ->
                            ctx.password = params.text
                        }
                    }
                }
                Text {
                    attr {
                        text(ctx.errorMsg)
                        fontSize(14f)
                        color(Color(0xFFFF3B30))
                        marginTop(8f)
                        marginLeft(40f)
                        alignSelfFlexStart()
                    }
                }
                Button {
                    attr {
                        size(pagerData.pageViewWidth * 0.85f, 48f)
                        marginTop(32f)
                        borderRadius(24f)
                        backgroundColor(Color(0xFF4A90D9L))
                        alignItemsCenter()
                        justifyContentCenter()
                    }
                    event {
                        click {
                            val uname = ctx.username.trim()
                            val pwd = ctx.password.trim()

                            if (uname.isEmpty()) {
                                ctx.errorMsg = "请输入用户名"
                                ctx.audioModule.vibrate(50)
                                return@click
                            }
                            if (pwd.isEmpty()) {
                                ctx.errorMsg = "请输入密码"
                                ctx.audioModule.vibrate(50)
                                return@click
                            }
                            if (pwd.length < 6) {
                                ctx.errorMsg = "密码长度不能少于6位"
                                ctx.audioModule.vibrate(50)
                                return@click
                            }

                            ctx.errorMsg = ""
                            ctx.loginSuccess = true
                            ctx.audioModule.playSound("login_success")
                            ctx.sp.setString("saved_username", uname)
                            ctx.sp.setString("saved_password", pwd)
                        }
                    }
                    Text {
                        attr {
                            text("登录")
                            fontSize(18f)
                            color(Color(0xFFFFFFFFL))
                            fontWeightMedium()
                        }
                    }
                }
                Text {
                    attr {
                        text(if (ctx.loginSuccess) "登录成功！" else "")
                        fontSize(16f)
                        color(Color(0xFF4CAF50))
                        marginTop(16f)
                    }
                }
            }
        }
    }
}