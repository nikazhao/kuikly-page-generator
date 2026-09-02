package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.views.compose.Button
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*

@Page("EmptyStatePage")
internal class EmptyStatePage : Pager() {

    override fun body(): ViewBuilder {
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Center {
                    View {
                        attr {
                            flexDirectionColumn()
                            alignItemsCenter()
                        }
                        Image {
                            attr {
                                size(120f, 120f)
                                src(ImageUri.pageAssets("empty_icon"))
                                marginBottom(24f)
                            }
                        }
                        Text {
                            attr {
                                text("暂无数据，请稍后重试")
                                fontSize(16f)
                                color(Color(0xFF999999))
                                textAlignCenter()
                                marginBottom(32f)
                            }
                        }
                        Button {
                            attr {
                                size(160f, 44f)
                                backgroundColor(Color(0xFF4A90D9))
                                borderRadius(22f)
                                titleAttr {
                                    text("重试")
                                    fontSize(16f)
                                    color(Color.WHITE)
                                }
                            }
                            event {
                                touchDown {
                                    // 重试逻辑
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}