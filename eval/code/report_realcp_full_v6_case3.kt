package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.module.NotifyModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("ProductDetailPage")
internal class ProductDetailPage : Pager() {

    private var productTitle by observable("")
    private var productPrice by observable("")
    private var productDescription by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    width(pagerData.pageViewWidth)
                    height(pagerData.pageViewHeight)
                }
                View {
                    attr {
                        flexDirection(FlexDirection.COLUMN)
                        width(pagerData.pageViewWidth)
                    }
                    Image {
                        attr {
                            width(pagerData.pageViewWidth)
                            height(300f)
                            src("https://example.com/product_image.jpg")
                        }
                    }
                    Text {
                        attr {
                            text("商品标题")
                            fontSize(20f)
                            fontWeightBold()
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                    }
                    Text {
                        attr {
                            text("¥199.00")
                            fontSize(24f)
                            color(Color(0xFFFF5722))
                            fontWeightBold()
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                    }
                    Text {
                        attr {
                            text("这是一段商品描述，详细介绍了该商品的特点、材质、尺寸等信息，方便用户了解商品详情。")
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginTop(12f)
                            marginLeft(16f)
                            marginRight(16f)
                            lineHeight(22f)
                        }
                    }
                    Button {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(48f)
                            marginTop(24f)
                            marginLeft(16f)
                            marginRight(16f)
                            marginBottom(32f)
                            backgroundColor(Color(0xFFFF5722))
                            borderRadius(24f)
                            titleAttr {
                                text("加入购物车")
                                fontSize(18f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchDown {
                                val notifyModule = ctx.acquireModule<NotifyModule>(NotifyModule.MODULE_NAME)
                                notifyModule.postNotify("addToCart", JSONObject().apply {
                                    put("title", ctx.productTitle)
                                    put("price", ctx.productPrice)
                                    put("description", ctx.productDescription)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}