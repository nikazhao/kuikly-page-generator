package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.SharedPreferencesModule
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

    private var productImageUrl by observable("")
    private var productTitle by observable("")
    private var productPrice by observable("")
    private var productDescription by observable("")

    private lateinit var sp: SharedPreferencesModule

    override fun created() {
        super.created()
        sp = acquireModule(SharedPreferencesModule.MODULE_NAME)
        productTitle = sp.getString("productTitle")
        productPrice = sp.getString("productPrice")
        productDescription = sp.getString("productDescription")
        productImageUrl = sp.getString("productImageUrl")
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            Scroller {
                attr {
                    flex(1f)
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Image {
                    attr {
                        width(pagerData.pageViewWidth)
                        height(pagerData.pageViewWidth * 0.75f)
                        src(ctx.productImageUrl.ifEmpty { "https://example.com/product_image.jpg" })
                    }
                }
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        padding(16f, 12f, 16f, 12f)
                        flexDirectionColumn()
                    }
                    Text {
                        attr {
                            text(ctx.productTitle.ifEmpty { "商品标题" })
                            fontSize(18f)
                            fontWeightBold()
                            color(Color(0xFF333333))
                            marginBottom(8f)
                        }
                    }
                    Text {
                        attr {
                            text(ctx.productPrice.ifEmpty { "¥199.00" })
                            fontSize(22f)
                            fontWeightBold()
                            color(Color(0xFFFF4444))
                            marginBottom(8f)
                        }
                    }
                    Text {
                        attr {
                            text(ctx.productDescription.ifEmpty { "这是一段商品描述信息，展示商品的特点和规格。" })
                            fontSize(14f)
                            color(Color(0xFF666666))
                            lineHeight(22f)
                        }
                    }
                }
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        padding(16f, 12f, 16f, 12f)
                        backgroundColor(Color(0xFFFFFFFF))
                    }
                    Button {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(48f)
                            borderRadius(24f)
                            backgroundColor(Color(0xFFFF5722))
                            titleAttr {
                                text("加入购物车")
                                fontSize(18f)
                                color(Color.WHITE)
                            }
                        }
                        event {
                            touchDown {
                                ctx.sp.setString("productTitle", ctx.productTitle)
                                ctx.sp.setString("productPrice", ctx.productPrice)
                                ctx.sp.setString("productDescription", ctx.productDescription)
                                ctx.sp.setString("productImageUrl", ctx.productImageUrl)
                            }
                        }
                    }
                }
            }
        }
    }
}