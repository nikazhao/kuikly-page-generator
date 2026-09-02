package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.layout.FlexDirection
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
    private var loginCount by observable(0)

    private lateinit var audioModule: AudioHapticsModule
    private lateinit var sp: SharedPreferencesModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)
        username = sp.getString("saved_username") ?: ""
        password = sp.getString("saved_password") ?: ""
        loginCount = sp.getInt("login_count") ?: 0
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFFFFFFF))
                }
                // 顶部导航栏
                View {
                    attr {
                        size(pagerData.pageViewWidth, 56f)
                        backgroundColor(Color(0xFFFFFFFF))
                        flexDirection(FlexDirection.ROW)
                        alignItemsCenter()
                        paddingTop(pagerData.safeAreaInsets.top)
                    }
                    View {
                        attr {
                            size(56f, 56f)
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("←")
                                fontSize(24f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("登录")
                                fontSize(18f)
                                color(Color(0xFF333333))
                                fontWeightMedium()
                            }
                        }
                    }
                    View {
                        attr {
                            size(56f, 56f)
                        }
                    }
                }
                // 登录表单区域
                View {
                    attr {
                        flex(1f)
                        paddingLeft(24f)
                        paddingRight(24f)
                        paddingTop(40f)
                    }
                    // 手机号输入
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 80f)
                            marginBottom(16f)
                        }
                        Text {
                            attr {
                                text("手机号")
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginBottom(8f)
                            }
                        }
                        View {
                            attr {
                                size(pagerData.pageViewWidth - 48f, 48f)
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(8f)
                                paddingLeft(16f)
                                paddingRight(16f)
                                justifyContentCenter()
                            }
                            Input {
                                attr {
                                    placeholder("请输入手机号")
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    keyboardTypeNumber()
                                }
                                event {
                                    textDidChange { params ->
                                        ctx.username = params.text
                                    }
                                }
                            }
                        }
                    }
                    // 密码输入
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 80f)
                            marginBottom(32f)
                        }
                        Text {
                            attr {
                                text("密码")
                                fontSize(14f)
                                color(Color(0xFF666666))
                                marginBottom(8f)
                            }
                        }
                        View {
                            attr {
                                size(pagerData.pageViewWidth - 48f, 48f)
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(8f)
                                paddingLeft(16f)
                                paddingRight(16f)
                                justifyContentCenter()
                            }
                            Input {
                                attr {
                                    placeholder("请输入密码")
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    keyboardTypePassword()
                                }
                                event {
                                    textDidChange { params ->
                                        ctx.password = params.text
                                    }
                                }
                            }
                        }
                    }
                    // 登录按钮
                    Button {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 48f)
                            backgroundColor(Color(0xFF4A90D9))
                            borderRadius(24f)
                            alignItemsCenter()
                            justifyContentCenter()
                            titleAttr {
                                text("登录")
                                fontSize(18f)
                                color(Color(0xFFFFFFFF))
                                fontWeightBold()
                            }
                        }
                        event {
                            touchDown {
                                if (ctx.username.isEmpty()) {
                                    ctx.errorMsg = "请输入手机号"
                                    ctx.audioModule.playSound("error")
                                    ctx.audioModule.vibrate(100)
                                } else if (ctx.password.isEmpty()) {
                                    ctx.errorMsg = "请输入密码"
                                    ctx.audioModule.playSound("error")
                                    ctx.audioModule.vibrate(100)
                                } else {
                                    ctx.errorMsg = ""
                                    ctx.loginCount += 1
                                    ctx.sp.setString("saved_username", ctx.username)
                                    ctx.sp.setString("saved_password", ctx.password)
                                    ctx.sp.setInt("login_count", ctx.loginCount)
                                    ctx.audioModule.playSound("login_success")
                                    ctx.audioModule.vibrate(50)
                                }
                            }
                        }
                    }
                    // 错误提示
                    vif({ ctx.errorMsg.isNotEmpty() }) {
                        Text {
                            attr {
                                text(ctx.errorMsg)
                                fontSize(14f)
                                color(Color(0xFFF44336))
                                marginTop(8f)
                                marginLeft(24f)
                            }
                        }
                    }
                    // 忘记密码
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 40f)
                            alignItemsCenter()
                            justifyContentCenter()
                            marginTop(16f)
                        }
                        Text {
                            attr {
                                text("忘记密码？")
                                fontSize(14f)
                                color(Color(0xFF4A90D9))
                                textAlignCenter()
                            }
                        }
                    }
                    // 其他登录方式
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 60f)
                            flexDirection(FlexDirection.ROW)
                            alignItemsCenter()
                            justifyContentCenter()
                            marginTop(40f)
                        }
                        View {
                            attr {
                                size(60f, 60f)
                                borderRadius(30f)
                                backgroundColor(Color(0xFFF5F5F5))
                                alignItemsCenter()
                                justifyContentCenter()
                                marginRight(24f)
                            }
                            Text {
                                attr {
                                    text("W")
                                    fontSize(24f)
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                        View {
                            attr {
                                size(60f, 60f)
                                borderRadius(30f)
                                backgroundColor(Color(0xFFF5F5F5))
                                alignItemsCenter()
                                justifyContentCenter()
                                marginLeft(24f)
                            }
                            Text {
                                attr {
                                    text("Q")
                                    fontSize(24f)
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                    }
                    // 注册账号
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 40f)
                            alignItemsCenter()
                            justifyContentCenter()
                            marginTop(24f)
                        }
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                            }
                            Text {
                                attr {
                                    text("还没有账号？")
                                    fontSize(14f)
                                    color(Color(0xFF999999))
                                }
                            }
                            Text {
                                attr {
                                    text("立即注册")
                                    fontSize(14f)
                                    color(Color(0xFF4A90D9))
                                    marginLeft(4f)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}