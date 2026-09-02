package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.timer.setTimeout
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
    private var isCodeButtonEnabled by observable(true)
    private var codeButtonText by observable("获取验证码")
    private var isRegisterButtonEnabled by observable(false)
    private var countdownSeconds by observable(60)
    private var phoneError by observable("")
    private var codeError by observable("")
    private var passwordError by observable("")

    private lateinit var sp: SharedPreferencesModule

    override fun created() {
        super.created()
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)
        phoneNumber = sp.getString("phoneNumber")
        verificationCode = sp.getString("verificationCode")
        password = sp.getString("password")
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        flex(1f)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            padding(24f, 24f, 24f, 24f)
                        }
                        Input {
                            attr {
                                width(pagerData.pageViewWidth - 48f)
                                height(48f)
                                marginTop(40f)
                                backgroundColor(Color(0xFFFFFFFF))
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                fontSize(16f)
                                placeholder("请输入手机号")
                                keyboardTypeNumber()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.phoneNumber = params.text ?: ""
                                    ctx.isCodeButtonEnabled = ctx.phoneNumber.length == 11
                                    ctx.isRegisterButtonEnabled = ctx.phoneNumber.length == 11 &&
                                        ctx.verificationCode.length >= 4 &&
                                        ctx.password.length >= 6
                                    if (ctx.phoneNumber.isNotEmpty() && ctx.phoneNumber.length != 11) {
                                        ctx.phoneError = "请输入11位手机号"
                                    } else {
                                        ctx.phoneError = ""
                                    }
                                    ctx.sp.setString("phoneNumber", ctx.phoneNumber)
                                }
                            }
                        }
                        Text {
                            attr {
                                text(ctx.phoneError)
                                fontSize(12f)
                                color(Color(0xFFE53935))
                                marginTop(4f)
                                marginLeft(8f)
                            }
                        }
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 48f)
                                height(48f)
                                marginTop(16f)
                                flexDirectionRow()
                                alignItemsCenter()
                            }
                            Input {
                                attr {
                                    flex(1f)
                                    height(48f)
                                    backgroundColor(Color(0xFFFFFFFF))
                                    borderRadius(8f)
                                    border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                    fontSize(16f)
                                    placeholder("请输入验证码")
                                    keyboardTypeNumber()
                                }
                                event {
                                    textDidChange { params ->
                                        ctx.verificationCode = params.text ?: ""
                                        ctx.isRegisterButtonEnabled = ctx.phoneNumber.length == 11 &&
                                            ctx.verificationCode.length >= 4 &&
                                            ctx.password.length >= 6
                                        if (ctx.verificationCode.isNotEmpty() && ctx.verificationCode.length < 4) {
                                            ctx.codeError = "验证码至少4位"
                                        } else {
                                            ctx.codeError = ""
                                        }
                                        ctx.sp.setString("verificationCode", ctx.verificationCode)
                                    }
                                }
                            }
                            Button {
                                attr {
                                    width(100f)
                                    height(48f)
                                    marginLeft(12f)
                                    backgroundColor(if (ctx.isCodeButtonEnabled) Color(0xFF4A90D9) else Color(0xFFBDBDBD))
                                    borderRadius(8f)
                                    titleAttr {
                                        text(ctx.codeButtonText)
                                        fontSize(14f)
                                        color(Color.WHITE)
                                    }
                                }
                                event {
                                    touchDown {
                                        if (ctx.phoneNumber.length != 11) {
                                            ctx.phoneError = "请输入正确的手机号"
                                            return@touchDown
                                        }
                                        ctx.isCodeButtonEnabled = false
                                        ctx.countdownSeconds = 60
                                        ctx.codeButtonText = "${ctx.countdownSeconds}s"
                                        ctx.startCountdown()
                                    }
                                }
                            }
                        }
                        Text {
                            attr {
                                text(ctx.codeError)
                                fontSize(12f)
                                color(Color(0xFFE53935))
                                marginTop(4f)
                                marginLeft(8f)
                            }
                        }
                        Input {
                            attr {
                                width(pagerData.pageViewWidth - 48f)
                                height(48f)
                                marginTop(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                fontSize(16f)
                                placeholder("请输入密码（至少6位）")
                                keyboardTypePassword()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.password = params.text ?: ""
                                    ctx.isRegisterButtonEnabled = ctx.phoneNumber.length == 11 &&
                                        ctx.verificationCode.length >= 4 &&
                                        ctx.password.length >= 6
                                    if (ctx.password.isNotEmpty() && ctx.password.length < 6) {
                                        ctx.passwordError = "密码至少6位"
                                    } else {
                                        ctx.passwordError = ""
                                    }
                                    ctx.sp.setString("password", ctx.password)
                                }
                            }
                        }
                        Text {
                            attr {
                                text(ctx.passwordError)
                                fontSize(12f)
                                color(Color(0xFFE53935))
                                marginTop(4f)
                                marginLeft(8f)
                            }
                        }
                        Button {
                            attr {
                                width(pagerData.pageViewWidth - 48f)
                                height(48f)
                                marginTop(32f)
                                backgroundColor(if (ctx.isRegisterButtonEnabled) Color(0xFF4A90D9) else Color(0xFFBDBDBD))
                                borderRadius(8f)
                                titleAttr {
                                    text("注册")
                                    fontSize(18f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    if (ctx.isRegisterButtonEnabled) {
                                        // 注册逻辑
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startCountdown() {
        setTimeout(1000) {
            countdownSeconds--
            if (countdownSeconds > 0) {
                codeButtonText = "${countdownSeconds}s"
                startCountdown()
            } else {
                codeButtonText = "获取验证码"
                isCodeButtonEnabled = true
            }
        }
    }
}