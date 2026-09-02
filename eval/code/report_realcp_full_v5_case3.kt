package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

@Page("ProductDetailPage")
internal class ProductDetailPage : Pager() {

    private var productTitle by observable("商品标题")
    private var productPrice by observable("¥99.00")
    private var productDescription by observable("这是一段商品描述文本，用于展示商品的详细信息，包括材质、尺寸、功能等。")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                        }
                        // 顶部大图
                        Image {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(pagerData.pageViewWidth * 0.75f)
                            }
                        }
                        // 商品标题
                        Text {
                            attr {
                                marginTop(16f)
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(20f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                text(ctx.productTitle)
                            }
                        }
                        // 商品价格
                        Text {
                            attr {
                                marginTop(12f)
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(24f)
                                fontWeightBold()
                                color(Color(0xFFFF4444))
                                text(ctx.productPrice)
                            }
                        }
                        // 商品描述
                        Text {
                            attr {
                                marginTop(12f)
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(14f)
                                color(Color(0xFF666666))
                                lines(3)
                                text(ctx.productDescription)
                            }
                        }
                        // 加入购物车按钮
                        Button {
                            attr {
                                marginTop(24f)
                                marginLeft(16f)
                                marginRight(16f)
                                marginBottom(32f)
                                height(48f)
                                backgroundColor(Color(0xFFFF4444))
                                borderRadius(24f)
                                titleAttr {
                                    text("加入购物车")
                                    fontSize(18f)
                                    color(Color.WHITE)
                                    fontWeightMedium()
                                }
                            }
                            event {
                                touchDown {
                                    // 加入购物车逻辑（可后续扩展为网络请求或本地存储）
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}