package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.network.NetworkModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.network.Callback
import com.tencent.kuikly.core.module.NetworkModule

@Page("RegisterPage")
internal class RegisterPage : Pager() {

    private var phoneNumber by observable("")
    private var code by observable("")
    private var password by observable("")
    private var codeButtonEnabled by observable(true)
    private var codeButtonText by observable("获取验证码")
    private var countdownSeconds by observable(60)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部标题
                Text {
                    attr {
                        text("注册")
                        fontSize(24f)
                        fontWeightBold()
                        color(Color(0xFF333333))
                        marginTop(40f)
                        marginLeft(20f)
                    }
                }
                // 中间表单区域
                View {
                    attr {
                        flex(1f)
                        marginTop(30f)
                        marginLeft(20f)
                        marginRight(20f)
                    }
                    // 手机号输入框
                    Input {
                        attr {
                            placeholder("请输入手机号")
                            fontSize(16f)
                            color(Color(0xFF333333))
                            placeholderColor(Color(0xFF999999))
                            keyboardTypeNumber()
                            height(50f)
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            marginBottom(15f)
                            marginLeft(15f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.phoneNumber = params.text
                            }
                        }
                    }
                    // 验证码行
                    View {
                        attr {
                            flexDirectionRow()
                            alignItemsCenter()
                            marginBottom(15f)
                        }
                        Input {
                            attr {
                                placeholder("请输入验证码")
                                fontSize(16f)
                                color(Color(0xFF333333))
                                placeholderColor(Color(0xFF999999))
                                keyboardTypeNumber()
                                height(50f)
                                flex(1f)
                                backgroundColor(Color.WHITE)
                                borderRadius(8f)
                                marginLeft(15f)
                            }
                            event {
                                textDidChange { params ->
                                    ctx.code = params.text
                                }
                            }
                        }
                        Button {
                            attr {
                                width(100f)
                                height(50f)
                                marginLeft(10f)
                                backgroundColor(Color(0xFF4A90D9))
                                borderRadius(8f)
                                titleAttr {
                                    text(ctx.codeButtonText)
                                    fontSize(14f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    if (ctx.codeButtonEnabled) {
                                        ctx.codeButtonEnabled = false
                                        ctx.codeButtonText = "${ctx.countdownSeconds}s"
                                        val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                        networkModule?.requestPost(
                                            "https://api.example.com/sendCode",
                                            JSONObject().apply {
                                                put("phone", ctx.phoneNumber)
                                            },
                                            object : Callback {
                                                override fun onResult(result: Any?) {
                                                    ctx.countdownSeconds = 60
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // 密码输入框
                    Input {
                        attr {
                            placeholder("请输入密码")
                            fontSize(16f)
                            color(Color(0xFF333333))
                            placeholderColor(Color(0xFF999999))
                            keyboardTypePassword()
                            height(50f)
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            marginLeft(15f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.password = params.text
                            }
                        }
                    }
                }
                // 底部注册按钮
                Button {
                    attr {
                        width(pagerData.pageViewWidth - 40f)
                        height(50f)
                        marginLeft(20f)
                        marginRight(20f)
                        marginBottom(30f)
                        backgroundColor(Color(0xFF4A90D9))
                        borderRadius(25f)
                        titleAttr {
                            text("注册")
                            fontSize(18f)
                            fontWeightBold()
                            color(Color.WHITE)
                        }
                    }
                    event {
                        touchDown {
                            val phone = ctx.phoneNumber
                            val code = ctx.code
                            val password = ctx.password
                            if (phone.isNotEmpty() && code.isNotEmpty() && password.isNotEmpty()) {
                                val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                networkModule?.requestPost(
                                    "https://api.example.com/register",
                                    JSONObject().apply {
                                        put("phone", phone)
                                        put("code", code)
                                        put("password", password)
                                    },
                                    object : Callback {
                                        override fun onResult(result: Any?) {
                                            // 处理注册结果
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}