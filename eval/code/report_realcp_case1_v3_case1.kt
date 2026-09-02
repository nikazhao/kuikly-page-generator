package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
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
    private var errorMsg by observable("")

    private var sp: SharedPreferencesModule? = null

    override fun createExternalModules(): Map<String, Module>? {
        return null
    }

    override fun created() {
        super.created()
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)
        username = sp?.getString("saved_username") ?: ""
        password = sp?.getString("saved_password") ?: ""
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(80f)
                        alignItemsCenter()
                        justifyContentCenter()
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
                Center {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth * 0.85f)
                            flexDirectionColumn()
                            alignItemsCenter()
                        }
                        Input {
                            attr {
                                width(pagerData.pageViewWidth * 0.85f)
                                height(48f)
                                placeholder("请输入用户名")
                                fontSize(16f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFFF5F5F5))
                                marginLeft(16f)
                                marginRight(16f)
                            }
                            event {
                                textDidChange { text ->
                                    ctx.username = text
                                }
                            }
                        }
                        View {
                            attr {
                                height(20f)
                            }
                        }
                        Input {
                            attr {
                                width(pagerData.pageViewWidth * 0.85f)
                                height(48f)
                                placeholder("请输入密码")
                                fontSize(16f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFFF5F5F5))
                                marginLeft(16f)
                                marginRight(16f)
                                keyboardTypePassword()
                            }
                            event {
                                textDidChange { text ->
                                    ctx.password = text
                                }
                            }
                        }
                        View {
                            attr {
                                height(30f)
                            }
                        }
                        Button {
                            attr {
                                width(pagerData.pageViewWidth * 0.85f)
                                height(48f)
                                borderRadius(24f)
                                backgroundColor(Color(0xFF4A90D9))
                                alignItemsCenter()
                                justifyContentCenter()
                            }
                            event {
                                click {
                                    if (ctx.username.isEmpty() || ctx.password.isEmpty()) {
                                        ctx.errorMsg = "请输入用户名和密码"
                                    } else {
                                        ctx.errorMsg = ""
                                        ctx.sp?.setString("saved_username", ctx.username)
                                        ctx.sp?.setString("saved_password", ctx.password)
                                    }
                                }
                            }
                            Text {
                                attr {
                                    text("登录")
                                    fontSize(18f)
                                    color(Color.WHITE)
                                    fontWeightMedium()
                                }
                            }
                        }
                        Text {
                            attr {
                                text(ctx.errorMsg)
                                fontSize(14f)
                                color(Color(0xFFFF3B30))
                                marginTop(8f)
                            }
                        }
                    }
                }
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(60f)
                    }
                }
            }
        }
    }
}