package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
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

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部标题区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(60f)
                        alignItemsCenter()
                        justifyContentCenter()
                        backgroundColor(Color.WHITE)
                    }
                    Text {
                        attr {
                            text("登录")
                            fontSize(20f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                        }
                    }
                }
                // 中间表单区域 - 使用 Center 居中
                Center {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth * 0.85f)
                            flexDirectionColumn()
                            alignItemsStretch()
                        }
                        // 用户名输入框
                        View {
                            attr {
                                width(pagerData.pageViewWidth * 0.85f)
                                height(50f)
                                backgroundColor(Color.WHITE)
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFDDDDDD)))
                                paddingLeft(16f)
                                paddingRight(16f)
                                justifyContentCenter()
                            }
                            Input {
                                attr {
                                    width(pagerData.pageViewWidth * 0.85f - 32f)
                                    height(48f)
                                    fontSize(16f)
                                    placeholder("请输入用户名")
                                    color(Color(0xFF333333))
                                }
                                event {
                                    textDidChange { params ->
                                        ctx.username = params.text
                                    }
                                }
                            }
                        }
                        // 间距
                        View {
                            attr {
                                height(16f)
                            }
                        }
                        // 密码输入框
                        View {
                            attr {
                                width(pagerData.pageViewWidth * 0.85f)
                                height(50f)
                                backgroundColor(Color.WHITE)
                                borderRadius(8f)
                                border(Border(1f, BorderStyle.SOLID, Color(0xFFDDDDDD)))
                                paddingLeft(16f)
                                paddingRight(16f)
                                justifyContentCenter()
                            }
                            Input {
                                attr {
                                    width(pagerData.pageViewWidth * 0.85f - 32f)
                                    height(48f)
                                    fontSize(16f)
                                    placeholder("请输入密码")
                                    color(Color(0xFF333333))
                                    keyboardTypePassword()
                                }
                                event {
                                    textDidChange { params ->
                                        ctx.password = params.text
                                    }
                                }
                            }
                        }
                        // 间距
                        View {
                            attr {
                                height(24f)
                            }
                        }
                        // 登录按钮
                        Button {
                            attr {
                                width(pagerData.pageViewWidth * 0.85f)
                                height(50f)
                                backgroundColor(Color(0xFF4A90D9))
                                borderRadius(25f)
                                titleAttr {
                                    text("登录")
                                    fontSize(18f)
                                    color(Color.WHITE)
                                    fontWeightBold()
                                }
                            }
                            event {
                                touchUp {
                                    ctx.handleLogin()
                                }
                            }
                        }
                    }
                }
                // 底部留白
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(40f)
                    }
                }
                // AlertDialog 模态弹窗 - 验证失败时显示
                AlertDialog {
                    attr {
                        showAlert(ctx.showAlert)
                        title("提示")
                        message(ctx.alertMessage)
                        actionButtons("确定")
                    }
                    event {
                        clickActionButton { index ->
                            ctx.showAlert = false
                        }
                    }
                }
            }
        }
    }

    private fun handleLogin() {
        if (username.isEmpty()) {
            alertMessage = "请输入用户名"
            showAlert = true
        } else if (password.isEmpty()) {
            alertMessage = "请输入密码"
            showAlert = true
        } else if (password.length < 6) {
            alertMessage = "密码长度不能少于6位"
            showAlert = true
        } else {
            // 验证通过，执行登录逻辑
        }
    }
}