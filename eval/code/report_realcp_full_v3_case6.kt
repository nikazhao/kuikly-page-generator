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
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("RegisterPage")
internal class RegisterPage : Pager() {

    private var phone by observable("")
    private var code by observable("")
    private var password by observable("")
    private var countdown by observable(0)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 标题区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingTop(60f)
                        paddingBottom(40f)
                        alignItemsCenter()
                    }
                    Text {
                        attr {
                            text("注册")
                            fontSize(28f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                        }
                    }
                }
                // 表单区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingLeft(24f)
                        paddingRight(24f)
                        flexDirectionColumn()
                    }
                    // 手机号输入项
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 48f)
                            height(50f)
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            marginBottom(16f)
                            paddingLeft(16f)
                            paddingRight(16f)
                            flexDirectionRow()
                            alignItemsCenter()
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(48f)
                                fontSize(16f)
                                placeholder("请输入手机号")
                                keyboardTypeNumber()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.phone = params?.text ?: ""
                                }
                            }
                        }
                    }
                    // 验证码输入项
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 48f)
                            height(50f)
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            marginBottom(16f)
                            paddingLeft(16f)
                            paddingRight(16f)
                            flexDirectionRow()
                            alignItemsCenter()
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(48f)
                                fontSize(16f)
                                placeholder("请输入验证码")
                                keyboardTypeNumber()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.code = params?.text ?: ""
                                }
                            }
                        }
                        Button {
                            attr {
                                width(100f)
                                height(36f)
                                marginLeft(12f)
                                backgroundColor(Color(0xFF4A90D9))
                                borderRadius(4f)
                                titleAttr {
                                    text("获取验证码")
                                    fontSize(14f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    ctx.countdown = 60
                                }
                            }
                        }
                    }
                    // 密码输入项
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 48f)
                            height(50f)
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            marginBottom(16f)
                            paddingLeft(16f)
                            paddingRight(16f)
                            flexDirectionRow()
                            alignItemsCenter()
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(48f)
                                fontSize(16f)
                                placeholder("请输入密码")
                                keyboardTypePassword()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.password = params?.text ?: ""
                                }
                            }
                        }
                    }
                }
                // 底部注册按钮
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingLeft(24f)
                        paddingRight(24f)
                        paddingTop(40f)
                        alignItemsCenter()
                    }
                    Button {
                        attr {
                            width(pagerData.pageViewWidth - 48f)
                            height(50f)
                            backgroundColor(Color(0xFF4A90D9))
                            borderRadius(25f)
                            titleAttr {
                                text("注册")
                                fontSize(18f)
                                color(Color.WHITE)
                                fontWeightBold()
                            }
                        }
                        event {
                            touchDown {
                                val params = JSONObject().apply {
                                    put("phone", ctx.phone)
                                    put("code", ctx.code)
                                    put("password", ctx.password)
                                }
                                val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                networkModule?.requestPost(
                                    "https://api.example.com/register",
                                    params
                                ) { response, success, msg ->
                                    // 处理注册结果
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}