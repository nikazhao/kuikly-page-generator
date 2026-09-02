package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

data class MenuItemData(
    val title: String,
    val icon: String = ""
)

@Page("UserProfilePage")
internal class UserProfilePage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("")
    private var signature by observable("")
    private var menuItems by observableList<MenuItemData>()

    override fun created() {
        super.created()
        nickname = "用户昵称"
        signature = "这个人很懒，什么都没留下~"
        menuItems.add(MenuItemData("我的收藏"))
        menuItems.add(MenuItemData("我的订单"))
        menuItems.add(MenuItemData("设置"))
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部头像区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(200f)
                        backgroundColor(Color(0xFFFFFFFF))
                    }
                    Center {
                        Image {
                            attr {
                                size(80f, 80f)
                                borderRadius(40f)
                                src(ImageUri.pageAssets("avatar_default"))
                            }
                        }
                    }
                }
                // 昵称和签名区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingTop(16f)
                        paddingBottom(16f)
                        alignItemsCenter()
                        backgroundColor(Color(0xFFFFFFFF))
                    }
                    Text {
                        attr {
                            text(ctx.nickname)
                            fontSize(20f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                        }
                    }
                    Text {
                        attr {
                            text(ctx.signature)
                            fontSize(14f)
                            color(Color(0xFF999999))
                            marginTop(8f)
                        }
                    }
                }
                // 底部功能菜单列表
                List {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                        backgroundColor(Color(0xFFFFFFFF))
                    }
                    vforLazy({ ctx.menuItems }) { item: MenuItemData, index: Int, count: Int ->
                        View {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(50f)
                                flexDirectionRow()
                                alignItemsCenter()
                                paddingLeft(16f)
                                paddingRight(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            event {
                                click {
                                    val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    when (item.title) {
                                        "我的收藏" -> router.openPage("FavoritePage")
                                        "我的订单" -> router.openPage("OrderPage")
                                        "设置" -> router.openPage("SettingsPage")
                                        else -> router.openPage("UnknownPage")
                                    }
                                }
                            }
                            Text {
                                attr {
                                    text(item.title)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text(">")
                                    fontSize(16f)
                                    color(Color(0xFFCCCCCC))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}