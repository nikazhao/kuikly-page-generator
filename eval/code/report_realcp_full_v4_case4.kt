package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.RouterModule
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
import com.tencent.kuikly.core.base.PositionType

data class MenuItem(
    val icon: String,
    val title: String
)

@Page("UserProfilePage")
internal class UserProfilePage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("")
    private var signature by observable("")
    private var menuList by observableList<MenuItem>()

    override fun created() {
        super.created()
        menuList.clear()
        menuList.addAll(
            listOf(
                MenuItem("icon_profile", "个人资料"),
                MenuItem("icon_favorites", "我的收藏"),
                MenuItem("icon_settings", "设置")
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
                View {
                    attr {
                        alignItemsCenter()
                        paddingTop(60f)
                        paddingBottom(30f)
                    }
                    Center {
                        Image {
                            attr {
                                size(80f, 80f)
                                borderRadius(40f)
                                src("user_avatar")
                            }
                        }
                    }
                    Text {
                        attr {
                            marginTop(12f)
                            fontSize(20f)
                            fontWeightBold()
                            text("用户昵称")
                        }
                    }
                    Text {
                        attr {
                            marginTop(6f)
                            fontSize(14f)
                            color(Color(0xFF999999))
                            text("这个人很懒，什么都没留下")
                        }
                    }
                }
                List {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    vforLazy({ ctx.menuList }) { item: MenuItem, index: Int, _: Int ->
                        View {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(56f)
                                flexDirectionRow()
                                alignItemsCenter()
                                paddingLeft(16f)
                                paddingRight(16f)
                                backgroundColor(Color.WHITE)
                            }
                            event {
                                click {
                                    ctx.onMenuItemClick(item)
                                }
                            }
                            Image {
                                attr {
                                    size(24f, 24f)
                                    src(item.icon)
                                }
                            }
                            Text {
                                attr {
                                    marginLeft(12f)
                                    fontSize(16f)
                                    text(item.title)
                                }
                            }
                            View {
                                attr {
                                    positionType(PositionType.ABSOLUTE)
                                    left(52f)
                                    bottom(0f)
                                    width(pagerData.pageViewWidth - 52f)
                                    height(0.5f)
                                    backgroundColor(Color(0xFFEEEEEE))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onMenuItemClick(item: MenuItem) {
        val router = acquireModule<RouterModule>(RouterModule.MODULE_NAME)
        when (item.title) {
            "个人资料" -> {
                router?.openPage("ProfileEditPage", null)
            }
            "我的收藏" -> {
                router?.openPage("FavoritesPage", null)
            }
            "设置" -> {
                router?.openPage("SettingsPage", null)
            }
        }
    }
}