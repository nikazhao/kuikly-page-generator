package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
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
    private var productPrice by observable("¥99.99")
    private var productDescription by observable("这是一段商品描述，介绍产品的特点和优势。")

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
                Scroller {
                    attr {
                        size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                        flex(1f)
                    }
                    Image {
                        attr {
                            size(pagerData.pageViewWidth, 300f)
                            src("https://example.com/product_image.jpg")
                        }
                    }
                    Text {
                        attr {
                            text(ctx.productTitle)
                            fontSize(20f)
                            fontWeightBold()
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                            color(Color(0xFF333333))
                        }
                    }
                    Text {
                        attr {
                            text(ctx.productPrice)
                            fontSize(18f)
                            color(Color(0xFFFF5722))
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                    }
                    Text {
                        attr {
                            text(ctx.productDescription)
                            fontSize(14f)
                            color(Color(0xFF666666))
                            marginTop(8f)
                            marginLeft(16f)
                            marginRight(16f)
                            marginBottom(16f)
                        }
                    }
                    Button {
                        attr {
                            size(200f, 48f)
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                            backgroundColor(Color(0xFF07C160))
                            borderRadius(8f)
                            titleAttr {
                                text("加入购物车")
                                fontSize(16f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchDown {
                                println("加入购物车: ${ctx.productTitle}, 价格: ${ctx.productPrice}")
                            }
                        }
                    }
                }
            }
        }
    }
}