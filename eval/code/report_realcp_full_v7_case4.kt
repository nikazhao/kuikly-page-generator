package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.base.attr.ImageUri

@Page("UserProfilePage")
internal class UserProfilePage : Pager() {

    private var nickname by observable("用户昵称")
    private var signature by observable("这个人很懒，什么都没留下")
    private var avatarUrl by observable("default_avatar.png")

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
                Center {
                    attr {
                        marginTop(40f)
                    }
                    Image {
                        attr {
                            size(80f, 80f)
                            borderRadius(40f)
                            src(ImageUri.commonAssets("${ctx.avatarUrl}"))
                        }
                        event {
                            click {
                                ctx.avatarUrl = "new_avatar_url"
                            }
                        }
                    }
                }
                Text {
                    attr {
                        marginTop(12f)
                        text(ctx.nickname)
                        fontSize(20f)
                        fontWeightBold()
                        color(Color(0xFF333333))
                        textAlignCenter()
                    }
                }
                Text {
                    attr {
                        marginTop(6f)
                        text(ctx.signature)
                        fontSize(14f)
                        color(Color(0xFF999999))
                        textAlignCenter()
                    }
                }
                List {
                    attr {
                        marginTop(30f)
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                        }
                        event {
                            click {
                                // 我的收藏点击事件
                            }
                        }
                        Text {
                            attr {
                                marginLeft(16f)
                                text("我的收藏")
                                fontSize(16f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(1f)
                            backgroundColor(Color(0xFFEEEEEE))
                            marginLeft(16f)
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                        }
                        event {
                            click {
                                // 我的订单点击事件
                            }
                        }
                        Text {
                            attr {
                                marginLeft(16f)
                                text("我的订单")
                                fontSize(16f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(1f)
                            backgroundColor(Color(0xFFEEEEEE))
                            marginLeft(16f)
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                        }
                        event {
                            click {
                                // 设置点击事件
                            }
                        }
                        Text {
                            attr {
                                marginLeft(16f)
                                text("设置")
                                fontSize(16f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(1f)
                            backgroundColor(Color(0xFFEEEEEE))
                            marginLeft(16f)
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(50f)
                            flexDirectionRow()
                            alignItemsCenter()
                            backgroundColor(Color.WHITE)
                        }
                        event {
                            click {
                                // 关于我们点击事件
                            }
                        }
                        Text {
                            attr {
                                marginLeft(16f)
                                text("关于我们")
                                fontSize(16f)
                                color(Color(0xFF333333))
                            }
                        }
                    }
                }
            }
        }
    }
}