package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

@Page("SearchPage")
internal class SearchPage : Pager() {

    private var searchText by observable("")
    private var hotTags by observableList<String>()

    override fun onCreatePager(pagerId: String, pageData: JSONObject) {
        super.onCreatePager(pagerId, pageData)
        hotTags.addAll(
            listOf(
                "热门推荐", "最新资讯", "科技前沿", "生活百科",
                "美食探店", "旅行攻略", "时尚穿搭", "影视娱乐",
                "健康养生", "教育学习"
            )
        )
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flex(1f)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Input {
                    attr {
                        marginTop(10f)
                        marginLeft(16f)
                        marginRight(16f)
                        height(44f)
                        borderRadius(22f)
                        backgroundColor(Color(0xFFFFFFFF))
                        placeholder("搜索热门标签")
                        fontSize(14f)
                        color(Color(0xFF333333))
                        placeholderColor(Color(0xFF999999))
                        paddingLeft(16f)
                        paddingRight(16f)
                    }
                    event {
                        textDidChange { params ->
                            ctx.searchText = params.text
                        }
                        inputReturn {
                            val keyword = ctx.searchText
                        }
                    }
                }
                Scroller {
                    attr {
                        flex(1f)
                        marginTop(10f)
                    }
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            flexWrapWrap()
                            paddingLeft(16f)
                            paddingRight(16f)
                        }
                        vfor({ ctx.hotTags }) { tag ->
                            View {
                                attr {
                                    marginRight(10f)
                                    marginBottom(10f)
                                    height(36f)
                                    borderRadius(18f)
                                    backgroundColor(Color(0xFFFFFFFF))
                                    border(Border(1f, BorderStyle.SOLID, Color(0xFFE0E0E0)))
                                    paddingLeft(16f)
                                    paddingRight(16f)
                                    alignItemsCenter()
                                    justifyContentCenter()
                                }
                                Text {
                                    attr {
                                        text(tag)
                                        fontSize(14f)
                                        color(Color(0xFF666666))
                                    }
                                }
                                event {
                                    click {
                                        ctx.searchText = tag
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