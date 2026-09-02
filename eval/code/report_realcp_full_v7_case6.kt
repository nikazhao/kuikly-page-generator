package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("RegisterPage")
internal class RegisterPage : Pager() {

    private var phoneNumber by observable("")
    private var verificationCode by observable("")
    private var password by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    paddingTop(pagerData.safeAreaInsets.top)
                    paddingBottom(pagerData.safeAreaInsets.bottom)
                    padding(pagerData.safeAreaInsets.left, 0f, 0f, pagerData.safeAreaInsets.right)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        flex(1f)
                    }
                    View {
                        attr {
                            flexDirectionColumn()
                            paddingLeft(24f)
                            paddingRight(24f)
                            paddingTop(40f)
                        }
                        Input {
                            attr {
                                height(48f)
                                marginBottom(16f)
                                backgroundColor(Color.WHITE)
                                borderRadius(8f)
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(16f)
                                placeholder("请输入手机号")
                                keyboardTypeNumber()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.phoneNumber = params.text
                                }
                            }
                        }
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                marginBottom(16f)
                            }
                            Input {
                                attr {
                                    flex(1f)
                                    height(48f)
                                    backgroundColor(Color.WHITE)
                                    borderRadius(8f)
                                    marginLeft(16f)
                                    marginRight(16f)
                                    fontSize(16f)
                                    placeholder("请输入验证码")
                                    keyboardTypeNumber()
                                }
                                event {
                                    textDidChange { params ->
                                        ctx.verificationCode = params.text
                                    }
                                }
                            }
                            Button {
                                attr {
                                    width(80f)
                                    height(48f)
                                    marginLeft(12f)
                                    backgroundColor(Color(0xFF4A90D9))
                                    borderRadius(8f)
                                    titleAttr {
                                        text("获取")
                                        fontSize(16f)
                                        color(Color.WHITE)
                                    }
                                }
                                event {
                                    touchDown {
                                        val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                        val params = JSONObject().apply {
                                            put("phone", ctx.phoneNumber)
                                        }
                                        networkModule.requestPost(
                                            "https://api.example.com/sendCode",
                                            params
                                        ) { response, success, msg ->
                                            if (success) {
                                                // 发送成功
                                            } else {
                                                // 发送失败
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Input {
                            attr {
                                height(48f)
                                marginBottom(32f)
                                backgroundColor(Color.WHITE)
                                borderRadius(8f)
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(16f)
                                placeholder("请输入密码")
                                keyboardTypePassword()
                            }
                            event {
                                textDidChange { params ->
                                    ctx.password = params.text
                                }
                            }
                        }
                        Center {
                            attr {
                                width(pagerData.pageViewWidth - 48f)
                                height(48f)
                            }
                            Button {
                                attr {
                                    width(200f)
                                    height(48f)
                                    backgroundColor(Color(0xFF4A90D9))
                                    borderRadius(24f)
                                    titleAttr {
                                        text("注册")
                                        fontSize(18f)
                                        color(Color.WHITE)
                                        fontWeightBold()
                                    }
                                }
                                event {
                                    touchDown {
                                        val networkModule = ctx.acquireModule<NetworkModule>(NetworkModule.MODULE_NAME)
                                        val spModule = ctx.acquireModule<SharedPreferencesModule>(SharedPreferencesModule.MODULE_NAME)
                                        val params = JSONObject().apply {
                                            put("phone", ctx.phoneNumber)
                                            put("code", ctx.verificationCode)
                                            put("password", ctx.password)
                                        }
                                        networkModule.requestPost(
                                            "https://api.example.com/register",
                                            params
                                        ) { response, success, msg ->
                                            if (success) {
                                                spModule.setString("token", response.optString("token", ""))
                                                spModule.setString("phone", ctx.phoneNumber)
                                            } else {
                                                // 注册失败
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}