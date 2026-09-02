package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.base.attr.ImageUri

@Page("UserProfilePage")
internal class UserProfilePage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("")
    private var signature by observable("")
    private var menuList by observable(
        listOf(
            MenuItem("我的订单", "order_icon", "order"),
            MenuItem("我的收藏", "favorite_icon", "favorite"),
            MenuItem("设置", "settings_icon", "settings")
        )
    )

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    }
                    Center {
                        attr {
                            marginTop(40f)
                        }
                        Image {
                            attr {
                                size(80f, 80f)
                                borderRadius(40f)
                                src(com.tencent.kuikly.core.base.attr.ImageUri.pageAssets("avatar_default"))
                            }
                            event {
                                click { params ->
                                    // 点击头像事件
                                    // 可以触发选择头像等操作
                                }
                            }
                        }
                    }
                    Text {
                        attr {
                            text("昵称")
                            fontSize(20f)
                            fontWeightBold()
                            textAlignCenter()
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                    }
                    Text {
                        attr {
                            text("这个人很懒，什么都没留下~")
                            fontSize(14f)
                            color(Color(0xFF999999))
                            textAlignCenter()
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                    }
                    View {
                        attr {
                            marginTop(40f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                height(50f)
                            }
                            Image {
                                attr {
                                    size(24f, 24f)
                                    src(com.tencent.kuikly.core.base.attr.ImageUri.pageAssets("icon_edit"))
                                }
                            }
                            Text {
                                attr {
                                    text("编辑资料")
                                    fontSize(16f)
                                    marginLeft(12f)
                                }
                            }
                            event {
                                click { params ->
                                    val menuIndex = 0
                                    if (menuIndex >= 0 && menuIndex < ctx.menuList.size) {
                                        val selectedMenu = ctx.menuList[menuIndex]
                                        val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        router.openPage(selectedMenu.route, null)
                                    }
                                }
                            }
                        }
                        View {
                            attr {
                                height(0.5f)
                                backgroundColor(Color(0xFFEEEEEE))
                                marginLeft(36f)
                            }
                        }
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                height(50f)
                            }
                            Image {
                                attr {
                                    size(24f, 24f)
                                    src(com.tencent.kuikly.core.base.attr.ImageUri.pageAssets("icon_setting"))
                                }
                            }
                            Text {
                                attr {
                                    text("设置")
                                    fontSize(16f)
                                    marginLeft(12f)
                                }
                            }
                            event {
                                click { params ->
                                    val menuIndex = 1
                                    if (menuIndex >= 0 && menuIndex < ctx.menuList.size) {
                                        val selectedMenu = ctx.menuList[menuIndex]
                                        val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        router.openPage(selectedMenu.route, null)
                                    }
                                }
                            }
                        }
                        View {
                            attr {
                                height(0.5f)
                                backgroundColor(Color(0xFFEEEEEE))
                                marginLeft(36f)
                            }
                        }
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                height(50f)
                            }
                            Image {
                                attr {
                                    size(24f, 24f)
                                    src(com.tencent.kuikly.core.base.attr.ImageUri.pageAssets("icon_about"))
                                }
                            }
                            Text {
                                attr {
                                    text("关于")
                                    fontSize(16f)
                                    marginLeft(12f)
                                }
                            }
                            event {
                                click { params ->
                                    val menuIndex = 2
                                    if (menuIndex >= 0 && menuIndex < ctx.menuList.size) {
                                        val selectedMenu = ctx.menuList[menuIndex]
                                        val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        router.openPage(selectedMenu.route, null)
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

data class MenuItem(
    val label: String,
    val icon: String,
    val route: String = ""
)