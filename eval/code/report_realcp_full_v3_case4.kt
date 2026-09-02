package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Scroller
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
    private var menuList by observableList<MenuItem>()

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
                // 顶部区域：头像、昵称、签名
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingTop(60f)
                        paddingBottom(30f)
                        flexDirectionColumn()
                    }
                    // 头像
                    Image {
                        attr {
                            size(80f, 80f)
                            borderRadius(40f)
                            src("avatar_default.png")
                            marginBottom(12f)
                        }
                        event {
                            click { params ->
                                // 点击头像事件
                            }
                        }
                    }
                    // 昵称
                    Text {
                        attr {
                            text("用户昵称")
                            fontSize(20f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            marginBottom(6f)
                        }
                    }
                    // 签名
                    Text {
                        attr {
                            text("这个人很懒，什么都没留下~")
                            fontSize(14f)
                            color(Color(0xFF999999))
                        }
                    }
                }
                // 底部区域：功能菜单列表
                Scroller {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    List {
                        attr {
                            flex(1f)
                            width(pagerData.pageViewWidth)
                        }
                        vfor({ ctx.menuList }) { item ->
                            View {
                                attr {
                                    width(pagerData.pageViewWidth)
                                    height(50f)
                                    flexDirectionRow()
                                    alignItemsCenter()
                                    backgroundColor(Color.WHITE)
                                    paddingLeft(16f)
                                    paddingRight(16f)
                                    border(Border(0.5f, BorderStyle.SOLID, Color(0xFFEEEEEE)))
                                }
                                event {
                                    click { params ->
                                        // 处理菜单点击
                                    }
                                }
                                Text {
                                    attr {
                                        flex(1f)
                                        text(item.title)
                                        fontSize(16f)
                                        color(Color(0xFF333333))
                                    }
                                }
                                Text {
                                    attr {
                                        text("›")
                                        fontSize(18f)
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
}