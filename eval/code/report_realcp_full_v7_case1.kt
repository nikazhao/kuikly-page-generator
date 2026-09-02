package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.AlertDialog
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

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var showAlert by observable(false)

    private lateinit var audioModule: AudioHapticsModule

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(AudioHapticsModule.MODULE_NAME to AudioHapticsModule())
    }

    override fun created() {
        super.created()
        audioModule = acquireModule(AudioHapticsModule.MODULE_NAME)
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部留白区域
                View {
                    attr {
                        flex(1f)
                    }
                }
                // 中间登录表单区域
                View {
                    attr {
                        flex(3f)
                        alignItemsCenter()
                        justifyContentCenter()
                        backgroundColor(Color(0xFFFFFFFF))
                        borderRadius(12f)
                        marginLeft(24f)
                        marginRight(24f)
                        paddingTop(32f)
                        paddingBottom(32f)
                        paddingLeft(24f)
                        paddingRight(24f)
                    }
                    // 用户名输入框
                    Input {
                        attr {
                            placeholder("请输入用户名")
                            width(280f)
                            height(48f)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                            marginBottom(16f)
                            paddingLeft(12f)
                            paddingRight(12f)
                            fontSize(16f)
                            backgroundColor(Color(0xFFF0F0F0))
                            color(Color(0xFF333333))
                        }
                        event {
                            textDidChange { params ->
                                ctx.username = params.text
                            }
                        }
                    }
                    // 密码输入框
                    Input {
                        attr {
                            placeholder("请输入密码")
                            width(280f)
                            height(48f)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                            marginBottom(24f)
                            paddingLeft(12f)
                            paddingRight(12f)
                            fontSize(16f)
                            keyboardTypePassword()
                            backgroundColor(Color(0xFFF0F0F0))
                            color(Color(0xFF333333))
                        }
                        event {
                            textDidChange { params ->
                                ctx.password = params.text
                            }
                        }
                    }
                    // 登录按钮
                    Button {
                        attr {
                            width(280f)
                            height(48f)
                            borderRadius(24f)
                            backgroundColor(Color(0xFF07C160))
                            titleAttr {
                                text("登录")
                                fontSize(18f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchDown {
                                ctx.onLoginClick()
                            }
                        }
                    }
                }
                // 底部留白区域
                View {
                    attr {
                        flex(1f)
                    }
                }
                // AlertDialog 条件渲染
                vif({ ctx.showAlert }) {
                    AlertDialog {
                        attr {
                            show(ctx.showAlert)
                            title("提示")
                            message("验证失败，请检查用户名和密码")
                            actionButtons("确定")
                        }
                        event {
                            clickActionButton { _ ->
                                ctx.showAlert = false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onLoginClick() {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert = true
            audioModule.vibrate(30)
        } else {
            // 登录成功逻辑
        }
    }
}