package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.*

@Page("EmptyStatePage")
internal class EmptyStatePage : Pager() {

    private var tipText by observable("")
    private var isLoading by observable(false)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    alignItemsCenter()
                    justifyContentCenter()
                }
                Image {
                    attr {
                        src(ImageUri.pageAssets("empty_icon"))
                        marginTop(0f)
                    }
                }
                Text {
                    attr {
                        text("暂无数据，请稍后重试")
                        fontSize(16f)
                        color(Color(0xFF999999))
                        marginTop(20f)
                        textAlignCenter()
                    }
                }
                Button {
                    attr {
                        marginTop(30f)
                        size(160f, 44f)
                        borderRadius(22f)
                        backgroundColor(Color(0xFF4A90D9))
                        titleAttr {
                            text("重试")
                            fontSize(16f)
                            color(Color.WHITE)
                        }
                    }
                    event {
                        touchDown {
                            if (!ctx.isLoading) {
                                ctx.isLoading = true
                            }
                        }
                    }
                }
            }
        }
    }
}