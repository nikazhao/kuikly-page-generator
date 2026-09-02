package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
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
    private var soundEnabled by observable(true)
    private var vibrationEnabled by observable(false)

    private lateinit var spModule: SharedPreferencesModule

    override fun createExternalModules(): Map<String, Module>? {
        return null
    }

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        notificationEnabled = spModule?.getBoolean("notificationEnabled", true) ?: true
        nightModeEnabled = spModule?.getBoolean("nightModeEnabled", false) ?: false
        soundEnabled = spModule?.getBoolean("soundEnabled", true) ?: true
        vibrationEnabled = spModule?.getBoolean("vibrationEnabled", false) ?: false
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
                        flexDirectionRow()
                        alignItemsCenter()
                        backgroundColor(Color.WHITE)
                        paddingTop(12f)
                        paddingBottom(12f)
                        paddingLeft(16f)
                        paddingRight(16f)
                    }
                    Image {
                        attr {
                            size(56f, 56f)
                            borderRadius(28f)
                            backgroundColor(Color(0xFFE0E0E0))
                            src(ImageUri.commonAssets("default_avatar.png"))
                        }
                        event {
                            click {
                                // 点击头像事件（可扩展为选择头像）
                            }
                        }
                    }
                    Text {
                        attr {
                            marginLeft(12f)
                            fontSize(18f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            text("用户昵称")
                        }
                        event {
                            click {
                                // 点击昵称事件（可扩展为编辑昵称）
                            }
                        }
                    }
                }
                Scroller {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            flexDirectionColumn()
                            backgroundColor(Color.WHITE)
                            marginTop(12f)
                        }
                        View {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(50f)
                                flexDirectionRow()
                                alignItemsCenter()
                                paddingLeft(16f)
                                paddingRight(16f)
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
                                    width(51f)
                                    height(31f)
                                    isOn(ctx.notificationEnabled)
                                }
                                event {
                                    switchChange { params: JSONObject ->
                                        ctx.notificationEnabled = params?.optBoolean("value", false) ?: false
                                        ctx.spModule?.setBoolean("notificationEnabled", ctx.notificationEnabled)
                                    }
                                }
                            }
                        }
                        View {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(50f)
                                flexDirectionRow()
                                alignItemsCenter()
                                paddingLeft(16f)
                                paddingRight(16f)
                                border(Border(0.5f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    text("深色模式")
                                }
                            }
                            Switch {
                                attr {
                                    width(51f)
                                    height(31f)
                                    isOn(ctx.nightModeEnabled)
                                }
                                event {
                                    switchChange { params: JSONObject ->
                                        ctx.nightModeEnabled = params?.optBoolean("value", false) ?: false
                                        ctx.spModule?.setBoolean("nightModeEnabled", ctx.nightModeEnabled)
                                    }
                                }
                            }
                        }
                        View {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(50f)
                                flexDirectionRow()
                                alignItemsCenter()
                                paddingLeft(16f)
                                paddingRight(16f)
                            }
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    text("省流量模式")
                                }
                            }
                            Switch {
                                attr {
                                    width(51f)
                                    height(31f)
                                    isOn(ctx.soundEnabled)
                                }
                                event {
                                    switchChange { params: JSONObject ->
                                        ctx.soundEnabled = params?.optBoolean("value", false) ?: false
                                        ctx.spModule?.setBoolean("soundEnabled", ctx.soundEnabled)
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