package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.module.Module
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

@Page("SettingsPage")
internal class SettingsPage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("")
    private var notificationEnabled by observable(true)
    private var nightModeEnabled by observable(false)
    private lateinit var spModule: SharedPreferencesModule

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        notificationEnabled = spModule.getInt("notificationEnabled")?.let { it == 1 } ?: true
        nightModeEnabled = spModule.getInt("nightModeEnabled")?.let { it == 1 } ?: false
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
                        size(pagerData.pageViewWidth, 120f)
                        backgroundColor(Color(0xFFFFFFFF))
                        flexDirectionRow()
                        alignItemsCenter()
                        padding(16f, 0f, 16f, 0f)
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
                            text("用户昵称")
                            fontSize(18f)
                            color(Color(0xFF333333))
                            fontWeightMedium()
                        }
                    }
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 200f)
                        backgroundColor(Color(0xFFF5F5F5))
                        flexDirectionColumn()
                        paddingTop(12f)
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 50f)
                            backgroundColor(Color(0xFFFFFFFF))
                            flexDirectionRow()
                            alignItemsCenter()
                            padding(16f, 0f, 16f, 0f)
                        }
                        Text {
                            attr {
                                text("消息通知")
                                fontSize(16f)
                                color(Color(0xFF333333))
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                            }
                        }
                        Switch {
                            attr {
                                onColor(Color(0xFF4A90D9))
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
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 50f)
                            backgroundColor(Color(0xFFFFFFFF))
                            flexDirectionRow()
                            alignItemsCenter()
                            padding(16f, 0f, 16f, 0f)
                        }
                        Text {
                            attr {
                                text("夜间模式")
                                fontSize(16f)
                                color(Color(0xFF333333))
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                            }
                        }
                        Switch {
                            attr {
                                onColor(Color(0xFF4A90D9))
                                unOnColor(Color(0xFFCCCCCC))
                            }
                            event {
                                switchOnChanged { isOn ->
                                    ctx.nightModeEnabled = isOn
                                    ctx.spModule.setInt("nightModeEnabled", if (isOn) 1 else 0)
                                }
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 50f)
                            backgroundColor(Color(0xFFFFFFFF))
                            flexDirectionRow()
                            alignItemsCenter()
                            padding(16f, 0f, 16f, 0f)
                        }
                        Text {
                            attr {
                                text("声音提醒")
                                fontSize(16f)
                                color(Color(0xFF333333))
                            }
                        }
                        View {
                            attr {
                                flex(1f)
                            }
                        }
                        Switch {
                            attr {
                                onColor(Color(0xFF4A90D9))
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