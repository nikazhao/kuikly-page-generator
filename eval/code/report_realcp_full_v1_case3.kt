package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

@Page("ProductDetailPage")
class ProductDetailPage : Pager() {

    private var productTitle by observable("商品标题")
    private var productPrice by observable("¥99.00")
    private var productDescription by observable("商品描述信息")
    private var isAddedToCart by observable(false)
    private var cartButtonText by observable("加入购物车")

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
                            flexDirection(FlexDirection.COLUMN)
                            width(pagerData.pageViewWidth)
                        }
                        // 顶部大图
                        Image {
                            attr {
                                width(pagerData.pageViewWidth)
                                height(300f)
                                src(ImageUri.pageAssets("product_detail_top_image"))
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
                        // 价格
                        Text {
                            attr {
                                marginTop(12f)
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(24f)
                                fontWeightBold()
                                color(Color(0xFFFF6B35))
                                text(ctx.productPrice)
                            }
                        }
                        // 描述
                        Text {
                            attr {
                                marginTop(12f)
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(14f)
                                color(Color(0xFF666666))
                                text(ctx.productDescription)
                            }
                        }
                        // 加入购物车按钮
                        Button {
                            attr {
                                marginTop(32f)
                                marginLeft(16f)
                                marginRight(16f)
                                height(48f)
                                backgroundColor(Color(0xFFFF6B35))
                                borderRadius(24f)
                                titleAttr {
                                    text(ctx.cartButtonText)
                                    fontSize(16f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchUp {
                                    if (!ctx.isAddedToCart) {
                                        ctx.isAddedToCart = true
                                        ctx.cartButtonText = "已加入购物车"
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