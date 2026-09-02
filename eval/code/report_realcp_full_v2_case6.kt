package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("RegisterPage")
internal class RegisterPage : Pager() {

    private var phoneNumber by observable("")
    private var verificationCode by observable("")
    private var password by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }
                View {
                    attr {
                        flex(1f)
                        flexDirectionColumn()
                    }
                    Text {
                        attr {
                            text("注册")
                            fontSize(24f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            marginTop(40f)
                            marginLeft(20f)
                            marginRight(20f)
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            marginTop(30f)
                            marginLeft(20f)
                            marginRight(20f)
                        }
                        Input {
                            attr {
                                placeholder("请输入手机号")
                                fontSize(16f)
                                color(Color(0xFF333333))
                                placeholderColor(Color(0xFF999999))
                                keyboardTypeNumber()
                                height(50f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                borderRadius(8f)
                                marginBottom(15f)
                                marginLeft(12f)
                                marginRight(12f)
                            }
                            event {
                                textDidChange { params ->
                                    ctx.phoneNumber = params.text
                                }
                            }
                        }
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
                                    border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                    borderRadius(8f)
                                    marginLeft(12f)
                                    marginRight(10f)
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
                                    height(50f)
                                    backgroundColor(Color(0xFF4A90D9))
                                    borderRadius(8f)
                                    titleAttr {
                                        text("获取验证码")
                                        fontSize(14f)
                                        color(Color.WHITE)
                                    }
                                }
                                event {
                                    touchDown {
                                        val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                        networkModule?.requestPost(
                                            "https://api.example.com/sendCode",
                                            JSONObject().apply {
                                                put("phone", ctx.phoneNumber)
                                            }
                                        ) { _, _, _ ->
                                        }
                                    }
                                }
                            }
                        }
                        Input {
                            attr {
                                placeholder("请输入密码")
                                fontSize(16f)
                                color(Color(0xFF333333))
                                placeholderColor(Color(0xFF999999))
                                keyboardTypePassword()
                                height(50f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                borderRadius(8f)
                                marginBottom(30f)
                                marginLeft(12f)
                                marginRight(12f)
                            }
                            event {
                                textDidChange { params ->
                                    ctx.password = params.text
                                }
                            }
                        }
                        Button {
                            attr {
                                height(50f)
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
                                    if (ctx.phoneNumber.isEmpty()) {
                                        return@touchDown
                                    }
                                    if (ctx.verificationCode.isEmpty()) {
                                        return@touchDown
                                    }
                                    if (ctx.password.isEmpty()) {
                                        return@touchDown
                                    }
                                    val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                    networkModule?.requestPost(
                                        "https://api.example.com/register",
                                        JSONObject().apply {
                                            put("phone", ctx.phoneNumber)
                                            put("code", ctx.verificationCode)
                                            put("password", ctx.password)
                                        }
                                    ) { _, _, _ ->
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}