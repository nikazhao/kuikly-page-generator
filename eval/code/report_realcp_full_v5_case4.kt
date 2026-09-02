package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.module.Module
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

data class MenuItem(
    val title: String,
    val icon: String
)

@Page("UserProfilePage")
internal class UserProfilePage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("")
    private var signature by observable("")
    private var menuItems by observableList<MenuItem>()

    override fun created() {
        super.created()
        avatarUrl = "avatar_default.png"
        nickname = "用户昵称"
        signature = "这个人很懒，什么都没留下~"
        menuItems.addAll(
            listOf(
                MenuItem("我的订单", "icon_order.png"),
                MenuItem("我的收藏", "icon_favorite.png"),
                MenuItem("设置", "icon_settings.png")
            )
        )
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
                // 顶部区域：圆形头像
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingTop(40f)
                        paddingBottom(20f)
                    }
                    Image {
                        attr {
                            size(80f, 80f)
                            borderRadius(40f)
                            src(ImageUri.commonAssets(ctx.avatarUrl))
                        }
                    }
                }
                // 昵称
                Text {
                    attr {
                        text(ctx.nickname)
                        fontSize(20f)
                        fontWeightBold()
                        color(Color(0xFF333333))
                        textAlignCenter()
                        width(pagerData.pageViewWidth)
                        marginTop(12f)
                    }
                }
                // 签名
                Text {
                    attr {
                        text(ctx.signature)
                        fontSize(14f)
                        color(Color(0xFF999999))
                        textAlignCenter()
                        width(pagerData.pageViewWidth)
                        marginTop(8f)
                        marginBottom(20f)
                    }
                }
                // 底部功能菜单列表
                List {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                        backgroundColor(Color.WHITE)
                    }
                    // 菜单项
                    for (item in ctx.menuItems) {
                        View {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(56f)
                                flexDirectionRow()
                                alignItemsCenter()
                                paddingLeft(16f)
                                paddingRight(16f)
                                border(Border(0.5f, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                            }
                            event {
                                click {
                                    val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    when (item.title) {
                                        "我的订单" -> router.openPage("OrderListPage", null)
                                        "我的收藏" -> router.openPage("FavoritePage", null)
                                        "设置" -> router.openPage("SettingsPage", null)
                                    }
                                }
                            }
                            Image {
                                attr {
                                    size(24f, 24f)
                                    src(ImageUri.commonAssets(item.icon))
                                    marginRight(12f)
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
                            Image {
                                attr {
                                    size(16f, 16f)
                                    src(ImageUri.commonAssets("icon_arrow_right.png"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}