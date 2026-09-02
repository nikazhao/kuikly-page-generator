package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexWrap
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*

@Page("SearchPage")
internal class SearchPage : Pager() {

    private var searchText by observable("")
    private var hotTags by observableList<String>()

    override fun onCreatePager(pagerId: String, pageData: JSONObject) {
        super.onCreatePager(pagerId, pageData)
        hotTags.addAll(
            listOf(
                "热门推荐", "最新资讯", "科技前沿", "生活百科",
                "美食探店", "旅行攻略", "时尚穿搭", "影视娱乐"
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
                // 顶部搜索输入框区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingTop(12f)
                        paddingBottom(12f)
                        paddingLeft(16f)
                        paddingRight(16f)
                        backgroundColor(Color.WHITE)
                    }
                    Input {
                        attr {
                            height(40f)
                            width(pagerData.pageViewWidth - 32f)
                            borderRadius(20f)
                            backgroundColor(Color(0xFFF0F0F0))
                            fontSize(14f)
                            placeholder("搜索热门标签")
                            placeholderColor(Color(0xFF999999))
                            marginLeft(0f)
                            marginRight(0f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.searchText = params?.text ?: ""
                            }
                        }
                    }
                }
                // 下方热门搜索标签区域
                Scroller {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                    }
                    View {
                        attr {
                            flexDirection(FlexDirection.ROW)
                            flexWrap(FlexWrap.WRAP)
                            paddingLeft(16f)
                            paddingRight(16f)
                            paddingTop(16f)
                        }
                        vfor({ ctx.hotTags }) { tag ->
                            Text {
                                attr {
                                    text(tag)
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                    backgroundColor(Color(0xFFF0F0F0))
                                    borderRadius(16f)
                                    marginLeft(12f)
                                    marginRight(12f)
                                    marginTop(6f)
                                    marginBottom(6f)
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