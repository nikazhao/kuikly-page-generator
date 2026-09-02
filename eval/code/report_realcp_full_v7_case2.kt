package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.layout.FlexAlign
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexJustifyContent
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Switch
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

@Page("SettingsPage")
internal class SettingsPage : Pager() {

    private var avatarUrl by observable("")
    private var userName by observable("")
    private var notificationEnabled by observable(false)
    private var darkModeEnabled by observable(false)
    private lateinit var spModule: SharedPreferencesModule

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        avatarUrl = spModule.getString("avatarUrl")
        userName = spModule.getString("userName")
        notificationEnabled = spModule.getInt("notificationEnabled") == 1
        darkModeEnabled = spModule.getInt("darkModeEnabled") == 1
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    flex(1f)
                }
                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        alignItems(FlexAlign.STRETCH)
                    }
                    // 用户信息区域
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            alignItems(FlexAlign.CENTER)
                            padding(16f, 12f, 16f, 12f)
                            backgroundColor(Color(0xFFFFFFFF))
                        }
                        Image {
                            attr {
                                size(60f, 60f)
                                borderRadius(30f)
                                src(ImageUri.commonAssets("avatar_default.png"))
                            }
                        }
                        Text {
                            attr {
                                marginLeft(12f)
                                fontSize(18f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                text("用户名")
                            }
                        }
                    }
                    // 设置开关列表区域
                    View {
                        attr {
                            flexDirection(FlexDirection.COLUMN)
                            marginTop(12f)
                            backgroundColor(Color(0xFFFFFFFF))
                        }
                        // 开关行 1
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItems(FlexAlign.CENTER)
                                justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                                padding(16f, 12f, 16f, 12f)
                                border(Border(0.5f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            }
                            Text {
                                attr {
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    text("消息通知")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(Color(0xFF4CAF50))
                                    unOnColor(Color(0xFFCCCCCC))
                                }
                                event {
                                    switchOnChanged { isOn ->
                                        ctx.notificationEnabled = isOn
                                        ctx.spModule.setInt("notificationEnabled", if (isOn) 1 else 0)
                                    }
                                }
                            }
                        }
                        // 开关行 2
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItems(FlexAlign.CENTER)
                                justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                                padding(16f, 12f, 16f, 12f)
                                border(Border(0.5f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            }
                            Text {
                                attr {
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    text("夜间模式")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(Color(0xFF4CAF50))
                                    unOnColor(Color(0xFFCCCCCC))
                                }
                                event {
                                    switchOnChanged { isOn ->
                                        ctx.darkModeEnabled = isOn
                                        ctx.spModule.setInt("darkModeEnabled", if (isOn) 1 else 0)
                                    }
                                }
                            }
                        }
                        // 开关行 3
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItems(FlexAlign.CENTER)
                                justifyContent(FlexJustifyContent.SPACE_BETWEEN)
                                padding(16f, 12f, 16f, 12f)
                                border(Border(0.5f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            }
                            Text {
                                attr {
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    text("震动反馈")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(Color(0xFF4CAF50))
                                    unOnColor(Color(0xFFCCCCCC))
                                }
                                event {
                                    switchOnChanged { isOn ->
                                        // 处理开关状态变化
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