package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Switch
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

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
        notificationEnabled = (spModule.getInt("notificationEnabled") ?: 0) == 1
        darkModeEnabled = (spModule.getInt("darkModeEnabled") ?: 0) == 1
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
                        flex(1f)
                        flexDirection(FlexDirection.COLUMN)
                    }
                    // 用户信息区
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            alignItemsCenter()
                            padding(16f, 12f, 16f, 12f)
                            backgroundColor(Color(0xFFFFFFFF))
                        }
                        Image {
                            attr {
                                size(48f, 48f)
                                borderRadius(24f)
                                src(ImageUri.commonAssets("default_avatar.png"))
                            }
                        }
                        Text {
                            attr {
                                marginLeft(12f)
                                fontSize(16f)
                                fontWeightMedium()
                                color(Color(0xFF333333))
                                text("用户昵称")
                            }
                        }
                    }
                    // 设置开关列表区
                    View {
                        attr {
                            flexDirection(FlexDirection.COLUMN)
                            marginTop(12f)
                            backgroundColor(Color(0xFFFFFFFF))
                        }
                        // 开关行1
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                                padding(16f, 12f, 16f, 12f)
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                    text("消息通知")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(Color(0xFF4A90D9))
                                    unOnColor(Color(0xFFE0E0E0))
                                }
                                event {
                                    switchOnChanged { isOn ->
                                        ctx.notificationEnabled = isOn
                                        ctx.spModule.setInt("notificationEnabled", if (isOn) 1 else 0)
                                    }
                                }
                            }
                        }
                        // 开关行2
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                                padding(16f, 12f, 16f, 12f)
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                    text("声音提醒")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(Color(0xFF4A90D9))
                                    unOnColor(Color(0xFFE0E0E0))
                                }
                                event {
                                    switchOnChanged { isOn ->
                                        // 处理开关状态变化
                                    }
                                }
                            }
                        }
                        // 开关行3
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                                padding(16f, 12f, 16f, 12f)
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                    text("震动反馈")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(Color(0xFF4A90D9))
                                    unOnColor(Color(0xFFE0E0E0))
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