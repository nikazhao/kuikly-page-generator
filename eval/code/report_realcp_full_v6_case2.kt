package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
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
    private var nickname by observable("")
    private var notificationEnabled by observable(false)
    private var nightModeEnabled by observable(false)
    private lateinit var spModule: SharedPreferencesModule

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        avatarUrl = spModule.getString("avatarUrl")
        nickname = spModule.getString("nickname")
        notificationEnabled = spModule.getInt("notification") == 1
        nightModeEnabled = spModule.getInt("nightMode") == 1
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 100f)
                        flexDirectionRow()
                        alignItemsCenter()
                        backgroundColor(Color(0xFFFFFFFF))
                        padding(16f, 12f, 16f, 12f)
                    }
                    Image {
                        attr {
                            size(60f, 60f)
                            borderRadius(30f)
                            src(ImageUri.commonAssets("default_avatar.png"))
                        }
                    }
                    Text {
                        attr {
                            marginLeft(16f)
                            fontSize(18f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            text("用户昵称")
                        }
                    }
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 200f)
                        flexDirectionColumn()
                        backgroundColor(Color(0xFFFFFFFF))
                        marginTop(12f)
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            padding(16f, 0f, 16f, 0f)
                        }
                        Text {
                            attr {
                                flex(1f)
                                fontSize(16f)
                                color(Color(0xFF333333))
                                text("消息通知")
                            }
                        }
                        Switch {
                            attr {
                                size(50f, 30f)
                                onColor(Color(0xFF4CD964))
                                unOnColor(Color(0xFFE5E5E5))
                            }
                            event {
                                switchOnChanged { isOn ->
                                    ctx.notificationEnabled = isOn
                                    ctx.spModule.setInt("notification", if (isOn) 1 else 0)
                                }
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            padding(16f, 0f, 16f, 0f)
                        }
                        Text {
                            attr {
                                flex(1f)
                                fontSize(16f)
                                color(Color(0xFF333333))
                                text("声音提醒")
                            }
                        }
                        Switch {
                            attr {
                                size(50f, 30f)
                                onColor(Color(0xFF4CD964))
                                unOnColor(Color(0xFFE5E5E5))
                            }
                            event {
                                switchOnChanged { isOn ->
                                    ctx.nightModeEnabled = isOn
                                    ctx.spModule.setInt("nightMode", if (isOn) 1 else 0)
                                }
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            padding(16f, 0f, 16f, 0f)
                        }
                        Text {
                            attr {
                                flex(1f)
                                fontSize(16f)
                                color(Color(0xFF333333))
                                text("震动提醒")
                            }
                        }
                        Switch {
                            attr {
                                size(50f, 30f)
                                onColor(Color(0xFF4CD964))
                                unOnColor(Color(0xFFE5E5E5))
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