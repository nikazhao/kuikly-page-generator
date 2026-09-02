package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.AlertDialog
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var showAlert by observable(false)
    private var errorMessage by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Text {
                    attr {
                        text("登录")
                        textAlignCenter()
                        marginTop(60f)
                        fontSize(24f)
                        fontWeightBold()
                        color(Color(0xFF333333))
                    }
                }
                View {
                    attr {
                        flex(1f)
                        justifyContentCenter()
                        alignItemsCenter()
                        padding(32f, 0f)
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth - 64f)
                            height(48f)
                            placeholder("请输入用户名")
                            fontSize(16f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                            borderRadius(8f)
                            marginBottom(16f)
                            marginLeft(12f)
                            backgroundColor(Color(0xFFFFFFFF))
                        }
                        event {
                            textDidChange { params ->
                                ctx.username = params.text
                            }
                        }
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth - 64f)
                            height(48f)
                            placeholder("请输入密码")
                            fontSize(16f)
                            keyboardTypePassword()
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                            borderRadius(8f)
                            marginBottom(32f)
                            marginLeft(12f)
                            backgroundColor(Color(0xFFFFFFFF))
                        }
                        event {
                            textDidChange { params ->
                                ctx.password = params.text
                            }
                        }
                    }
                    Button {
                        attr {
                            width(200f)
                            height(48f)
                            backgroundColor(Color(0xFF4A90D9))
                            borderRadius(24f)
                            titleAttr {
                                text("登录")
                                fontSize(18f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchUp {
                                ctx.onLoginClick()
                            }
                        }
                    }
                }
                AlertDialog {
                    attr {
                        showAlert(ctx.showAlert)
                        title("提示")
                        message(ctx.errorMessage)
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

    private fun onLoginClick() {
        if (username.isEmpty()) {
            errorMessage = "请输入用户名"
            showAlert = true
        } else if (password.isEmpty()) {
            errorMessage = "请输入密码"
            showAlert = true
        } else {
            // 验证通过，可执行登录操作（此处仅演示验证）
            // 实际登录需调用后端 API
        }
    }
}