package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.pager.Pager

/**
 * 登录页面示例
 * - 表单布局 (View + Text + Image)
 * - 响应式状态 (observable)
 * - 点击事件 (event { click { } })
 */
@Page("LoginPage")
internal class LoginPage : Pager() {

    private var username by observable("")
    private var password by observable("")
    private var errorMsg by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                allCenter()
            }

            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }

                // Logo
                Image {
                    attr {
                        size(80f, 80f)
                        src("logo.png")
                        marginTop(80f)
                        allCenter()
                    }
                }

                // 标题
                Text {
                    attr {
                        text("欢迎登录")
                        fontSize(24f)
                        fontWeightBold()
                        marginTop(16f)
                        allCenter()
                    }
                }

                // 用户名输入
                Text {
                    attr {
                        text(ctx.username.ifEmpty { "请输入用户名" })
                        fontSize(16f)
                        color(Color(0xFF999999))
                        marginTop(40f)
                        marginLeft(40f)
                        marginRight(40f)
                        height(48f)
                        backgroundColor(Color.WHITE)
                        borderRadius(8f)
                        paddingLeft(16f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.username = "user_demo"
                        }
                    }
                }

                // 密码输入
                Text {
                    attr {
                        text(ctx.password.ifEmpty { "请输入密码" })
                        fontSize(16f)
                        color(Color(0xFF999999))
                        marginTop(12f)
                        marginLeft(40f)
                        marginRight(40f)
                        height(48f)
                        backgroundColor(Color.WHITE)
                        borderRadius(8f)
                        paddingLeft(16f)
                        allCenter()
                    }
                    event {
                        click {
                            ctx.password = "******"
                        }
                    }
                }

                // 错误提示
                Text {
                    attr {
                        text(ctx.errorMsg)
                        fontSize(14f)
                        color(Color(0xFFFF3B30))
                        marginTop(8f)
                        marginLeft(40f)
                    }
                }

                // 登录按钮
                View {
                    attr {
                        size(pagerData.pageViewWidth - 80f, 48f)
                        marginTop(24f)
                        marginLeft(40f)
                        backgroundColor(Color(0xFF07C160))
                        borderRadius(8f)
                        allCenter()
                    }
                    event {
                        click {
                            if (ctx.username.isEmpty() || ctx.password.isEmpty()) {
                                ctx.errorMsg = "用户名和密码不能为空"
                            } else {
                                ctx.errorMsg = ""
                                // 跳转首页
                            }
                        }
                    }
                    Text {
                        attr {
                            text("登录")
                            fontSize(18f)
                            color(Color.WHITE)
                            fontWeightBold()
                        }
                    }
                }
            }
        }
    }
}
