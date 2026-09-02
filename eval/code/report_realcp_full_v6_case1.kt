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
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var showAlert by observable(false)
    private var alertMessage by observable("")

    private fun onLoginClick() {
        if (username.isEmpty() || password.isEmpty()) {
            alertMessage = "用户名和密码不能为空"
            showAlert = true
        }
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(60f)
                    }
                    Text {
                        attr {
                            text("登录")
                            fontSize(24f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                        }
                    }
                }
                View {
                    attr {
                        flex(1f)
                        flexDirectionColumn()
                        padding(24f, 0f, 24f, 0f)
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth - 48f)
                            height(48f)
                            placeholder("请输入用户名")
                            fontSize(16f)
                            color(Color(0xFF333333))
                            placeholderColor(Color(0xFF999999))
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            marginTop(24f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.username = params.text
                            }
                        }
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth - 48f)
                            height(48f)
                            placeholder("请输入密码")
                            fontSize(16f)
                            color(Color(0xFF333333))
                            placeholderColor(Color(0xFF999999))
                            backgroundColor(Color.WHITE)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            marginTop(16f)
                            keyboardTypePassword()
                        }
                        event {
                            textDidChange { params ->
                                ctx.password = params.text
                            }
                        }
                    }
                    Button {
                        attr {
                            width(pagerData.pageViewWidth - 48f)
                            height(48f)
                            marginTop(32f)
                            borderRadius(8f)
                            backgroundColor(Color(0xFF4A90D9))
                            titleAttr {
                                text("登录")
                                fontSize(18f)
                                color(Color.WHITE)
                                fontWeightMedium()
                            }
                        }
                        event {
                            touchDown {
                                ctx.onLoginClick()
                            }
                        }
                    }
                }
                View {
                    attr {
                        height(40f)
                    }
                }
                AlertDialog {
                    attr {
                        showAlert(ctx.showAlert)
                        title("提示")
                        message(ctx.alertMessage)
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