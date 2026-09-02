package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Switch
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

object SettingsPageStyles {
    val backgroundColor = Color(0xFFF5F5F5)
    val userInfoBackgroundColor = Color(0xFFFFFFFF)
    val nicknameTextColor = Color(0xFF333333)
    val settingTitleTextColor = Color(0xFF333333)
    val switchOnColor = Color(0xFF07C160)
    val switchOffColor = Color(0xFFE0E0E0)
    val dividerColor = Color(0xFFE0E0E0)

    val nicknameFontSize = 18f
    val settingTitleFontSize = 16f

    val userInfoPaddingVertical = 20f
    val userInfoPaddingHorizontal = 16f
    val avatarSize = 60f
    val avatarBorderRadius = 30f
    val avatarNicknameSpacing = 12f
    val settingItemHeight = 50f
    val settingItemPaddingHorizontal = 16f
    val dividerHeight = 0.5f
}

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
        notificationEnabled = spModule.getInt("notification") == 1
        nightModeEnabled = spModule.getInt("nightMode") == 1
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    backgroundColor(SettingsPageStyles.backgroundColor)
                }
                Scroller {
                    attr {
                        flex(1f)
                    }
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            alignItemsCenter()
                            padding(SettingsPageStyles.userInfoPaddingHorizontal, SettingsPageStyles.userInfoPaddingVertical, SettingsPageStyles.userInfoPaddingHorizontal, SettingsPageStyles.userInfoPaddingVertical)
                            backgroundColor(SettingsPageStyles.userInfoBackgroundColor)
                            marginBottom(8f)
                        }
                        Image {
                            attr {
                                size(SettingsPageStyles.avatarSize, SettingsPageStyles.avatarSize)
                                borderRadius(SettingsPageStyles.avatarBorderRadius)
                                src(ImageUri.commonAssets("default_avatar.png"))
                            }
                        }
                        Text {
                            attr {
                                marginLeft(SettingsPageStyles.avatarNicknameSpacing)
                                fontSize(SettingsPageStyles.nicknameFontSize)
                                fontWeightBold()
                                color(SettingsPageStyles.nicknameTextColor)
                                text("用户昵称")
                            }
                        }
                    }
                    View {
                        attr {
                            flexDirection(FlexDirection.COLUMN)
                            backgroundColor(Color.WHITE)
                        }
                        View {
                            attr {
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                                padding(SettingsPageStyles.settingItemPaddingHorizontal, 14f, SettingsPageStyles.settingItemPaddingHorizontal, 14f)
                                height(SettingsPageStyles.settingItemHeight)
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(SettingsPageStyles.settingTitleFontSize)
                                    color(SettingsPageStyles.settingTitleTextColor)
                                    text("消息通知")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(SettingsPageStyles.switchOnColor)
                                    unOnColor(SettingsPageStyles.switchOffColor)
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
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                                padding(SettingsPageStyles.settingItemPaddingHorizontal, 14f, SettingsPageStyles.settingItemPaddingHorizontal, 14f)
                                height(SettingsPageStyles.settingItemHeight)
                                border(Border(SettingsPageStyles.dividerHeight, BorderStyle.SOLID, SettingsPageStyles.dividerColor))
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(SettingsPageStyles.settingTitleFontSize)
                                    color(SettingsPageStyles.settingTitleTextColor)
                                    text("深色模式")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(SettingsPageStyles.switchOnColor)
                                    unOnColor(SettingsPageStyles.switchOffColor)
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
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                                padding(SettingsPageStyles.settingItemPaddingHorizontal, 14f, SettingsPageStyles.settingItemPaddingHorizontal, 14f)
                                height(SettingsPageStyles.settingItemHeight)
                                border(Border(SettingsPageStyles.dividerHeight, BorderStyle.SOLID, SettingsPageStyles.dividerColor))
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(SettingsPageStyles.settingTitleFontSize)
                                    color(SettingsPageStyles.settingTitleTextColor)
                                    text("声音")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(SettingsPageStyles.switchOnColor)
                                    unOnColor(SettingsPageStyles.switchOffColor)
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
                                flexDirection(FlexDirection.ROW)
                                alignItemsCenter()
                                padding(SettingsPageStyles.settingItemPaddingHorizontal, 14f, SettingsPageStyles.settingItemPaddingHorizontal, 14f)
                                height(SettingsPageStyles.settingItemHeight)
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(SettingsPageStyles.settingTitleFontSize)
                                    color(SettingsPageStyles.settingTitleTextColor)
                                    text("震动")
                                }
                            }
                            Switch {
                                attr {
                                    onColor(SettingsPageStyles.switchOnColor)
                                    unOnColor(SettingsPageStyles.switchOffColor)
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