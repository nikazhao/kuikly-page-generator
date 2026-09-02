package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.AlertDialog
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Border
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.base.Border

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var showAlert by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                }
                View {
                    attr {
                        flexDirectionColumn()
                        flex(1f)
                    }
                    // 顶部标题区域
                    Center {
                        attr {
                            height(60f)
                        }
                        Text {
                            attr {
                                text("登录")
                                fontSize(24f)
                                fontWeightBold()
                            }
                        }
                    }
                    // 用户名输入区域
                    View {
                        attr {
                            marginLeft(20f)
                            marginRight(20f)
                            marginTop(30f)
                        }
                        Text {
                            attr {
                                text("用户名")
                                fontSize(16f)
                                marginBottom(8f)
                            }
                        }
                        Input {
                            attr {
                                height(48f)
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                                margin(12f, 0f, 12f, 0f)
                                fontSize(16f)
                            }
                            event {
                                textDidChange { params ->
                                    ctx.username = params.text
                                }
                            }
                        }
                    }
                    // 密码输入区域
                    View {
                        attr {
                            marginLeft(20f)
                            marginRight(20f)
                            marginTop(20f)
                        }
                        Text {
                            attr {
                                text("密码")
                                fontSize(16f)
                                marginBottom(8f)
                            }
                        }
                        Input {
                            attr {
                                height(48f)
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                                margin(12f, 0f, 12f, 0f)
                                fontSize(16f)
                                keyboardTypePassword()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.password = params.text
                                }
                            }
                        }
                    }
                    // 登录按钮区域
                    Center {
                        attr {
                            marginLeft(20f)
                            marginRight(20f)
                            marginTop(40f)
                        }
                        Button {
                            attr {
                                height(48f)
                                borderRadius(24f)
                                backgroundColor(Color(0xFF4A90D9))
                                titleAttr {
                                    text("登录")
                                    fontSize(18f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    val username = ctx.username.trim()
                                    val password = ctx.password.trim()

                                    if (username.isEmpty()) {
                                        return@touchDown
                                    }
                                    if (password.isEmpty()) {
                                        return@touchDown
                                    }
                                    if (password.length < 6) {
                                        return@touchDown
                                    }

                                    ctx.showAlert = true
                                }
                            }
                        }
                    }
                    // AlertDialog 弹窗
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
}