package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.AlertDialog
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.*

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var showAlert by observable(false)

    fun onLoginClick() {
        if (username.isEmpty()) {
            return
        }
        if (password.isEmpty()) {
            return
        }
        showAlert = true
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirection(FlexDirection.COLUMN)
                    justifyContentCenter()
                    alignItemsCenter()
                }
                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        alignItemsCenter()
                    }
                    Input {
                        attr {
                            width(260f)
                            height(44f)
                            placeholder("请输入用户名")
                            fontSize(16f)
                            borderRadius(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            marginBottom(16f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.username = params.text
                            }
                        }
                    }
                    Input {
                        attr {
                            width(260f)
                            height(44f)
                            placeholder("请输入密码")
                            fontSize(16f)
                            borderRadius(8f)
                            backgroundColor(Color(0xFFF5F5F5))
                            keyboardTypePassword()
                            marginBottom(24f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.password = params.text
                            }
                        }
                    }
                    Button {
                        attr {
                            width(260f)
                            height(48f)
                            borderRadius(24f)
                            backgroundColor(Color(0xFF4A90D9))
                            titleAttr {
                                text("登录")
                                fontSize(18f)
                                color(Color.WHITE)
                                fontWeightBold()
                            }
                        }
                        event {
                            touchDown {
                                ctx.onLoginClick()
                            }
                        }
                    }
                }
                vif({ ctx.showAlert }) {
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
}