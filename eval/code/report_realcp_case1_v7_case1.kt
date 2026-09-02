package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
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
                    alignItemsCenter()
                    justifyContentCenter()
                }
                Input {
                    attr {
                        width(280f)
                        height(44f)
                        marginBottom(16f)
                        placeholder("请输入用户名")
                        fontSize(16f)
                        border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                        borderRadius(8f)
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
                        width(280f)
                        height(44f)
                        marginBottom(24f)
                        placeholder("请输入密码")
                        fontSize(16f)
                        keyboardTypePassword()
                        border(Border(1f, BorderStyle.SOLID, Color(0xFFCCCCCC)))
                        borderRadius(8f)
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
                        backgroundColor(Color(0xFF4A90D9))
                        borderRadius(24f)
                        titleAttr {
                            text("登录")
                            fontSize(18f)
                            color(Color.WHITE)
                            fontWeightBold()
                        }
                    }
                    event {
                        touchUp {
                            ctx.showAlert = true
                        }
                    }
                }
                vif({ ctx.showAlert }) {
                    AlertDialog {
                        attr {
                            title("提示")
                            message("登录成功！")
                        }
                        event {
                            confirm {
                                ctx.showAlert = false
                            }
                            cancel {
                                ctx.showAlert = false
                            }
                        }
                    }
                }
            }
        }
    }
}