package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Switch
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

data class SwitchItem(
    val name: String,
    val isOn: Boolean = false
)

@Page("SettingsPage")
internal class SettingsPage : Pager() {

    private var userName by observable("")
    private var switchStates by observableList<SwitchItem>()

    private lateinit var spModule: SharedPreferencesModule

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        userName = spModule.getString("userName")
        val initialList = mutableListOf(
            SwitchItem("通知", spModule.getInt("switch_0") == 1),
            SwitchItem("夜间模式", spModule.getInt("switch_1") == 1),
            SwitchItem("震动反馈", spModule.getInt("switch_2") == 1),
            SwitchItem("声音", spModule.getInt("switch_3") == 1)
        )
        switchStates.clear()
        switchStates.addAll(initialList)
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                View {
                    attr {
                        flexDirectionRow()
                        alignItemsCenter()
                        padding(16f, 12f, 16f, 12f)
                        backgroundColor(Color(0xFFFFFFFF))
                    }
                    Image {
                        attr {
                            size(60f, 60f)
                            borderRadius(30f)
                            marginRight(12f)
                            src("https://via.placeholder.com/60")
                        }
                    }
                    Text {
                        attr {
                            text(ctx.userName.ifEmpty { "用户昵称" })
                            fontSize(18f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                        }
                    }
                }
                View {
                    attr {
                        flexDirectionColumn()
                        paddingLeft(16f)
                        paddingRight(16f)
                        paddingTop(8f)
                        backgroundColor(Color(0xFFF5F5F5))
                    }
                    vfor({ ctx.switchStates }) { item ->
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                justifyContentSpaceBetween()
                                padding(14f, 0f, 14f, 0f)
                                backgroundColor(Color(0xFFFFFFFF))
                                borderRadius(8f)
                                marginBottom(1f)
                            }
                            Text {
                                attr {
                                    text(item.name)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                }
                            }
                            Switch {
                                attr {
                                    onColor(Color(0xFF4CD964))
                                    unOnColor(Color(0xFFE5E5E5))
                                }
                                event {
                                    switchOnChanged { isOn ->
                                        val index = ctx.switchStates.indexOf(item)
                                        if (index >= 0) {
                                            val newList = ctx.switchStates.toMutableList()
                                            newList[index] = newList[index].copy(isOn = isOn)
                                            ctx.switchStates.clear()
                                            ctx.switchStates.addAll(newList)
                                            ctx.spModule.setInt("switch_$index", if (isOn) 1 else 0)
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