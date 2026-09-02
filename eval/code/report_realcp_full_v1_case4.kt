package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vfor
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

data class MenuItem(
    val title: String,
    val route: String
)

@Page("UserProfilePage")
internal class UserProfilePage : Pager() {

    private var avatarUrl by observable("")
    private var nickname by observable("用户昵称")
    private var signature by observable("这个人很懒，什么都没留下")
    private var menuList by observableList<MenuItem>()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }
                // 顶部区域：头像 + 昵称 + 签名
                View {
                    attr {
                        flex(0f)
                        alignItemsCenter()
                        paddingTop(40f)
                        paddingBottom(24f)
                    }
                    Center {
                        attr {
                            size(80f, 80f)
                            borderRadius(40f)
                            backgroundColor(Color(0xFFE0E0E0))
                        }
                        Image {
                            attr {
                                size(80f, 80f)
                                borderRadius(40f)
                                src(ImageUri.commonAssets("default_avatar.png"))
                            }
                        }
                    }
                    Text {
                        attr {
                            marginTop(12f)
                            fontSize(20f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            text("用户昵称")
                        }
                    }
                    Text {
                        attr {
                            marginTop(6f)
                            fontSize(14f)
                            color(Color(0xFF999999))
                            text("这个人很懒，什么都没留下~")
                        }
                    }
                }
                // 底部区域：功能菜单列表
                List {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    vfor({ ctx.menuList }) { item: MenuItem ->
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
                            Text {
                                attr {
                                    flex(1f)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    text(item.title)
                                }
                            }
                            Image {
                                attr {
                                    size(16f, 16f)
                                    src(ImageUri.commonAssets("arrow_right.png"))
                                }
                            }
                            event {
                                click {
                                    val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    router?.openPage(item.route, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}