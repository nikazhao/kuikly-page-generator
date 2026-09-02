package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
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
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirection(FlexDirection.COLUMN)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 标题区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(60f)
                        alignItemsCenter()
                        justifyContentCenter()
                    }
                    Text {
                        attr {
                            text("用户注册")
                            fontSize(24f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                        }
                    }
                }
                // 表单区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                        padding(20f, 0f, 20f, 0f)
                    }
                    // 用户名输入项
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 40f)
                            height(50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginTop(20f)
                        }
                        Text {
                            attr {
                                text("用户名")
                                fontSize(16f)
                                color(Color(0xFF333333))
                                width(80f)
                            }
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(44f)
                                backgroundColor(Color(0xFFFFFFFF))
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                marginLeft(12f)
                                fontSize(15f)
                                placeholder("请输入用户名")
                            }
                            event {
                                textDidChange { params ->
                                    ctx.phoneNumber = params.text
                                }
                            }
                        }
                    }
                    // 密码输入项
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 40f)
                            height(50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginTop(16f)
                        }
                        Text {
                            attr {
                                text("密码")
                                fontSize(16f)
                                color(Color(0xFF333333))
                                width(80f)
                            }
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(44f)
                                backgroundColor(Color(0xFFFFFFFF))
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                marginLeft(12f)
                                fontSize(15f)
                                placeholder("请输入密码")
                                keyboardTypePassword()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.password = params.text
                                }
                            }
                        }
                    }
                    // 验证码输入项
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 40f)
                            height(50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            marginTop(16f)
                        }
                        Text {
                            attr {
                                text("验证码")
                                fontSize(16f)
                                color(Color(0xFF333333))
                                width(80f)
                            }
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(44f)
                                backgroundColor(Color(0xFFFFFFFF))
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                marginLeft(12f)
                                fontSize(15f)
                                placeholder("请输入验证码")
                            }
                            event {
                                textDidChange { params ->
                                    ctx.verificationCode = params.text
                                }
                            }
                        }
                        Button {
                            attr {
                                height(44f)
                                width(100f)
                                marginLeft(10f)
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
                                    networkModule.requestPost(
                                        "https://api.example.com/sendCode",
                                        JSONObject().apply {
                                            put("phone", ctx.phoneNumber)
                                        }
                                    ) { response, success, msg ->
                                        // 处理获取验证码结果
                                    }
                                }
                            }
                        }
                    }
                }
                // 底部注册按钮
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(100f)
                        alignItemsCenter()
                        justifyContentCenter()
                    }
                    Button {
                        attr {
                            width(300f)
                            height(48f)
                            backgroundColor(Color(0xFF4A90D9))
                            borderRadius(24f)
                            titleAttr {
                                text("注册")
                                fontSize(18f)
                                color(Color.WHITE)
                                fontWeightBold()
                            }
                        }
                        event {
                            touchDown {
                                val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                networkModule.requestPost(
                                    "https://api.example.com/register",
                                    JSONObject().apply {
                                        put("phone", ctx.phoneNumber)
                                        put("code", ctx.verificationCode)
                                        put("password", ctx.password)
                                    }
                                ) { response, success, msg ->
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("HomePage", JSONObject())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}