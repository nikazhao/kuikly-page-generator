package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.views.*
import kotlinx.coroutines.*

@Page("RegisterPage")
internal class RegisterPage : Pager() {

    private var phone by observable("")
    private var code by observable("")
    private var password by observable("")
    private var codeButtonEnabled by observable(true)
    private var codeButtonText by observable("获取验证码")
    private var countdownSeconds by observable(60)
    private var countdownTimer: Job? = null

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 标题区域
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(60f)
                        marginTop(40f)
                    }
                    Text {
                        attr {
                            text("注册")
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
                        marginLeft(20f)
                        marginRight(20f)
                        marginTop(30f)
                        flexDirectionColumn()
                    }
                    // 手机号输入框
                    Input {
                        attr {
                            height(48f)
                            width(pagerData.pageViewWidth - 40f)
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            marginLeft(12f)
                            marginRight(12f)
                            fontSize(16f)
                            placeholder("请输入手机号")
                            keyboardTypeNumber()
                        }
                        event {
                            textDidChange { params ->
                                ctx.phone = params.text
                            }
                        }
                    }
                    // 验证码行
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 40f)
                            height(48f)
                            marginTop(16f)
                            flexDirectionRow()
                            alignItemsCenter()
                        }
                        Input {
                            attr {
                                height(48f)
                                flex(1f)
                                backgroundColor(Color.WHITE)
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                marginLeft(12f)
                                marginRight(12f)
                                fontSize(16f)
                                placeholder("请输入验证码")
                                keyboardTypeNumber()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.code = params.text
                                }
                            }
                        }
                        Button {
                            attr {
                                width(90f)
                                height(48f)
                                marginLeft(12f)
                                backgroundColor(Color(0xFF4A90D9))
                                borderRadius(8f)
                                titleAttr {
                                    text(ctx.codeButtonText)
                                    fontSize(16f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    if (!ctx.codeButtonEnabled) return@touchDown

                                    if (ctx.phone.length != 11) {
                                        return@touchDown
                                    }

                                    ctx.codeButtonEnabled = false
                                    ctx.countdownSeconds = 60
                                    ctx.codeButtonText = "${ctx.countdownSeconds}s"

                                    ctx.countdownTimer = MainScope().launch {
                                        while (ctx.countdownSeconds > 0) {
                                            delay(1000)
                                            ctx.countdownSeconds--
                                            ctx.codeButtonText = "${ctx.countdownSeconds}s"
                                        }
                                        ctx.codeButtonEnabled = true
                                        ctx.codeButtonText = "重新获取"
                                    }
                                }
                            }
                        }
                    }
                    // 密码输入框
                    Input {
                        attr {
                            height(48f)
                            width(pagerData.pageViewWidth - 40f)
                            marginTop(16f)
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            marginLeft(12f)
                            marginRight(12f)
                            fontSize(16f)
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
                // 底部注册按钮
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        marginTop(40f)
                    }
                    Button {
                        attr {
                            width(pagerData.pageViewWidth - 80f)
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
                                if (ctx.phone.isEmpty()) {
                                    return@touchDown
                                }
                                if (ctx.code.isEmpty()) {
                                    return@touchDown
                                }
                                if (ctx.password.length < 6) {
                                    return@touchDown
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}