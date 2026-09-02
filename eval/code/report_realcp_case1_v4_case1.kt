package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.views.*

@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var errorMsg by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部导航栏区域
                View {
                    attr {
                        size(pagerData.pageViewWidth, 56f)
                        backgroundColor(Color.WHITE)
                        flexDirectionRow()
                        alignItemsCenter()
                        paddingTop(pagerData.safeAreaInsets.top)
                    }
                    View {
                        attr {
                            size(56f, 56f)
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("←")
                                fontSize(20f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("登录")
                                fontSize(18f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            size(56f, 56f)
                        }
                    }
                }
                // 主内容区域
                View {
                    attr {
                        flex(1f)
                        paddingLeft(24f)
                        paddingRight(24f)
                        paddingTop(40f)
                    }
                    // 标题
                    Text {
                        attr {
                            text("欢迎回来")
                            fontSize(28f)
                            fontWeightBold()
                            color(Color(0xFF1A1A1A))
                            marginBottom(8f)
                        }
                    }
                    Text {
                        attr {
                            text("请登录您的账号")
                            fontSize(14f)
                            color(Color(0xFF999999))
                            marginBottom(40f)
                        }
                    }
                    // 手机号输入框
                    Text {
                        attr {
                            text("手机号")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginBottom(8f)
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 48f)
                            backgroundColor(Color(0xFFF8F8F8))
                            borderRadius(12f)
                            flexDirectionRow()
                            alignItemsCenter()
                            paddingLeft(16f)
                            paddingRight(16f)
                            marginBottom(16f)
                        }
                        Text {
                            attr {
                                text("+86")
                                fontSize(14f)
                                color(Color(0xFF333333))
                                marginRight(8f)
                            }
                        }
                        View {
                            attr {
                                width(1f)
                                height(20f)
                                backgroundColor(Color(0xFFDDDDDD))
                                marginRight(12f)
                            }
                        }
                        Input {
                            attr {
                                flex(1f)
                                fontSize(14f)
                                color(Color(0xFF333333))
                                placeholder("请输入手机号")
                                placeholderColor(Color(0xFFCCCCCC))
                                keyboardTypePhonePad()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.username = params.text
                                }
                            }
                        }
                    }
                    // 密码输入框
                    Text {
                        attr {
                            text("密码")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginBottom(8f)
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 48f)
                            backgroundColor(Color(0xFFF8F8F8))
                            borderRadius(12f)
                            flexDirectionRow()
                            alignItemsCenter()
                            paddingLeft(16f)
                            paddingRight(16f)
                            marginBottom(24f)
                        }
                        Input {
                            attr {
                                flex(1f)
                                fontSize(14f)
                                color(Color(0xFF333333))
                                placeholder("请输入密码")
                                placeholderColor(Color(0xFFCCCCCC))
                                keyboardTypePassword()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.password = params.text
                                }
                            }
                        }
                        Text {
                            attr {
                                text("👁")
                                fontSize(18f)
                                color(Color(0xFF999999))
                            }
                        }
                    }
                    // 错误提示
                    Text {
                        attr {
                            text(ctx.errorMsg)
                            fontSize(13f)
                            color(Color(0xFFFF3B30))
                            marginBottom(8f)
                        }
                    }
                    // 忘记密码
                    View {
                        attr {
                            alignItemsFlexEnd()
                            marginBottom(32f)
                        }
                        Text {
                            attr {
                                text("忘记密码？")
                                fontSize(13f)
                                color(Color(0xFF4A90D9))
                            }
                        }
                    }
                    // 登录按钮
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 48f)
                            backgroundColor(Color(0xFF4A90D9))
                            borderRadius(24f)
                            alignItemsCenter()
                            justifyContentCenter()
                            marginBottom(16f)
                        }
                        event {
                            click {
                                if (ctx.username.isEmpty()) {
                                    ctx.errorMsg = "请输入手机号"
                                    return@click
                                }
                                if (ctx.password.isEmpty()) {
                                    ctx.errorMsg = "请输入密码"
                                    return@click
                                }
                                if (ctx.username != "admin" || ctx.password != "123456") {
                                    ctx.errorMsg = "手机号或密码错误"
                                    return@click
                                }
                                ctx.errorMsg = ""
                                // 登录成功逻辑
                            }
                        }
                        Text {
                            attr {
                                text("登录")
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color.WHITE)
                            }
                        }
                    }
                    // 注册按钮
                    View {
                        attr {
                            size(pagerData.pageViewWidth - 48f, 48f)
                            backgroundColor(Color.WHITE)
                            borderRadius(24f)
                            alignItemsCenter()
                            justifyContentCenter()
                            border(Border(1f, color = Color(0xFF4A90D9)))
                            marginBottom(40f)
                        }
                        Text {
                            attr {
                                text("注册账号")
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color(0xFF4A90D9))
                            }
                        }
                    }
                    // 其他登录方式
                    View {
                        attr {
                            flexDirectionRow()
                            alignItemsCenter()
                            marginBottom(24f)
                        }
                        View {
                            attr {
                                flex(1f)
                                height(1f)
                                backgroundColor(Color(0xFFDDDDDD))
                            }
                        }
                        Text {
                            attr {
                                text(" 其他登录方式 ")
                                fontSize(12f)
                                color(Color(0xFF999999))
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                                height(1f)
                                backgroundColor(Color(0xFFDDDDDD))
                            }
                        }
                    }
                    // 社交登录图标
                    View {
                        attr {
                            flexDirectionRow()
                            justifyContentCenter()
                            alignItemsCenter()
                        }
                        View {
                            attr {
                                size(44f, 44f)
                                borderRadius(22f)
                                backgroundColor(Color(0xFF07C160))
                                alignItemsCenter()
                                justifyContentCenter()
                                marginRight(24f)
                            }
                            Text {
                                attr {
                                    text("微")
                                    fontSize(18f)
                                    fontWeightBold()
                                    color(Color.WHITE)
                                }
                            }
                        }
                        View {
                            attr {
                                size(44f, 44f)
                                borderRadius(22f)
                                backgroundColor(Color(0xFF1E80FF))
                                alignItemsCenter()
                                justifyContentCenter()
                                marginRight(24f)
                            }
                            Text {
                                attr {
                                    text("Q")
                                    fontSize(18f)
                                    fontWeightBold()
                                    color(Color.WHITE)
                                }
                            }
                        }
                        View {
                            attr {
                                size(44f, 44f)
                                borderRadius(22f)
                                backgroundColor(Color(0xFFFF6600))
                                alignItemsCenter()
                                justifyContentCenter()
                            }
                            Text {
                                attr {
                                    text("支")
                                    fontSize(18f)
                                    fontWeightBold()
                                    color(Color.WHITE)
                                }
                            }
                        }
                    }
                }
                // 底部协议
                View {
                    attr {
                        alignItemsCenter()
                        paddingBottom(24f)
                        paddingBottom(pagerData.safeAreaInsets.bottom)
                    }
                    Text {
                        attr {
                            text("登录即表示同意《用户协议》和《隐私政策》")
                            fontSize(12f)
                            color(Color(0xFF999999))
                            textAlignCenter()
                        }
                    }
                }
            }
        }
    }
}