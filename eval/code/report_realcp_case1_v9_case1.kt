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
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var showAlert by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }
                View {
                    attr {
                        alignItemsCenter()
                        justifyContentCenter()
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    }
                    Input {
                        attr {
                            placeholder("请输入用户名")
                            marginBottom(16f)
                            width(280f)
                            height(48f)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                            marginLeft(12f)
                            marginRight(12f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.username = params.text
                            }
                        }
                    }
                    Input {
                        attr {
                            placeholder("请输入密码")
                            keyboardTypePassword()
                            marginBottom(24f)
                            width(280f)
                            height(48f)
                            borderRadius(8f)
                            border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
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
                            width(280f)
                            height(48f)
                            borderRadius(8f)
                            backgroundColor(Color(0xFF4A90D9))
                            titleAttr {
                                text("登录")
                                fontSize(18f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchUp {
                                ctx.showAlert = true
                            }
                        }
                    }
                }
                AlertDialog {
                    attr {
                        showAlert(ctx.showAlert)
                        title("提示")
                        message("登录成功")
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