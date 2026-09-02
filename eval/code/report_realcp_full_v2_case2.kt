package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Switch
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

object SettingsPageStyles {
    val bgColor = Color(0xFFF5F5F5)
    val userInfoBg = Color(0xFFFFFFFF)
    val userInfoRadius = 12f
    val userInfoPadding = 16f
    val avatarSize = 60f
    val avatarRadius = 30f
    val nickFontSize = 18f
    val nickColor = Color(0xFF333333)
    val itemHeight = 50f
    val itemBg = Color(0xFFFFFFFF)
    val itemBorderColor = Color(0xFFE0E0E0)
    val itemBorderWidth = 0.5f
    val labelFontSize = 16f
    val labelColor = Color(0xFF333333)
    val switchOnColor = Color(0xFF07C160)
    val switchUnOnColor = Color(0xFFE0E0E0)
}

@Page("SettingsPage")
internal class SettingsPage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("")
    private var notificationEnabled by observable(false)
    private var nightModeEnabled by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    flex(1f)
                }
                View {
                    attr {
                        flexDirectionRow()
                        alignItemsCenter()
                        justifyContentCenter()
                        paddingTop(20f)
                        paddingBottom(20f)
                    }
                    Image {
                        attr {
                            size(60f, 60f)
                            borderRadius(30f)
                            marginRight(12f)
                            src("user_avatar.png")
                        }
                    }
                    Text {
                        attr {
                            text("用户名")
                            fontSize(18f)
                            fontWeightBold()
                        }
                    }
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 1f)
                        backgroundColor(Color(0xFFE0E0E0))
                    }
                }
                View {
                    attr {
                        flexDirectionRow()
                        alignItemsCenter()
                        justifyContentSpaceBetween()
                        paddingLeft(16f)
                        paddingRight(16f)
                        height(56f)
                    }
                    Text {
                        attr {
                            text("消息通知")
                            fontSize(16f)
                        }
                    }
                    Switch {
                        attr {
                            size(51f, 31f)
                            onColor(Color(0xFF4CD964))
                        }
                        event {
                            switchOnChanged { isOn ->
                                ctx.notificationEnabled = isOn
                            }
                        }
                    }
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 1f)
                        backgroundColor(Color(0xFFE0E0E0))
                    }
                }
                View {
                    attr {
                        flexDirectionRow()
                        alignItemsCenter()
                        justifyContentSpaceBetween()
                        paddingLeft(16f)
                        paddingRight(16f)
                        height(56f)
                    }
                    Text {
                        attr {
                            text("声音提醒")
                            fontSize(16f)
                        }
                    }
                    Switch {
                        attr {
                            size(51f, 31f)
                            onColor(Color(0xFF4CD964))
                        }
                        event {
                            switchOnChanged { isOn ->
                                // 处理开关状态变化
                            }
                        }
                    }
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 1f)
                        backgroundColor(Color(0xFFE0E0E0))
                    }
                }
                View {
                    attr {
                        flexDirectionRow()
                        alignItemsCenter()
                        justifyContentSpaceBetween()
                        paddingLeft(16f)
                        paddingRight(16f)
                        height(56f)
                    }
                    Text {
                        attr {
                            text("震动反馈")
                            fontSize(16f)
                        }
                    }
                    Switch {
                        attr {
                            size(51f, 31f)
                            onColor(Color(0xFF4CD964))
                        }
                        event {
                            switchOnChanged { isOn ->
                                // 处理开关状态变化
                            }
                        }
                    }
                }
                View {
                    attr {
                        size(pagerData.pageViewWidth, 1f)
                        backgroundColor(Color(0xFFE0E0E0))
                    }
                }
                View {
                    attr {
                        flexDirectionRow()
                        alignItemsCenter()
                        justifyContentSpaceBetween()
                        paddingLeft(16f)
                        paddingRight(16f)
                        height(56f)
                    }
                    Text {
                        attr {
                            text("夜间模式")
                            fontSize(16f)
                        }
                    }
                    Switch {
                        attr {
                            size(51f, 31f)
                            onColor(Color(0xFF4CD964))
                        }
                        event {
                            switchOnChanged { isOn ->
                                ctx.nightModeEnabled = isOn
                            }
                        }
                    }
                }
            }
        }
    }
}