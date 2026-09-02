package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*

data class MenuItemData(
    val icon: String,
    val title: String,
    val onClick: (() -> Unit)? = null
)

@Page("UserProfilePage")
internal class UserProfilePage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("")
    private var signature by observable("")
    private var menuList by observableList<MenuItemData>()

    override fun created() {
        super.created()
        nickname = "用户昵称"
        signature = "这个人很懒，什么都没留下~"
        menuList.addAll(
            listOf(
                MenuItemData("icon_profile.png", "个人资料") {
                    // 点击个人资料
                },
                MenuItemData("icon_settings.png", "设置") {
                    // 点击设置
                },
                MenuItemData("icon_about.png", "关于") {
                    // 点击关于
                }
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
                // 顶部区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        flexDirectionColumn()
                        alignItemsCenter()
                        paddingTop(40f)
                        paddingBottom(24f)
                    }
                    // 头像
                    Center {
                        attr {
                            size(80f, 80f)
                            borderRadius(40f)
                            backgroundColor(Color(0xFFE0E0E0))
                        }
                        Image {
                            attr {
                                size(80f, 80f)
                                src(ImageUri.commonAssets("default_avatar.png"))
                            }
                        }
                    }
                    // 昵称
                    Text {
                        attr {
                            marginTop(12f)
                            fontSize(20f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            text(ctx.nickname)
                        }
                    }
                    // 签名
                    Text {
                        attr {
                            marginTop(6f)
                            fontSize(14f)
                            color(Color(0xFF999999))
                            text(ctx.signature)
                        }
                    }
                }
                // 底部菜单列表
                List {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                        backgroundColor(Color.WHITE)
                    }
                    // 菜单项
                    ctx.menuList.forEach { menuItem ->
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
                                    menuItem.onClick?.invoke()
                                }
                            }
                            Image {
                                attr {
                                    size(24f, 24f)
                                    src(ImageUri.commonAssets(menuItem.icon))
                                }
                            }
                            Text {
                                attr {
                                    marginLeft(12f)
                                    flex(1f)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    text(menuItem.title)
                                }
                            }
                            Text {
                                attr {
                                    fontSize(14f)
                                    color(Color(0xFFCCCCCC))
                                    text(">")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}