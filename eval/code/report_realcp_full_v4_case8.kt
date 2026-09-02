package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.directives.vfor

@Page("SearchPage")
internal class SearchPage : Pager() {

    private var searchText by observable("")
    private var hotTags by observableList<String>()
    private var selectedTag by observable("")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                // 顶部搜索输入框区域
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        padding(12f, 8f, 12f, 8f)
                        backgroundColor(Color.WHITE)
                    }
                    Input {
                        attr {
                            width(pagerData.pageViewWidth - 24f)
                            height(40f)
                            borderRadius(20f)
                            backgroundColor(Color(0xFFF0F0F0))
                            marginLeft(16f)
                            marginRight(16f)
                            fontSize(14f)
                            placeholder("搜索热门标签")
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
                            flexDirectionRow()
                            flexWrapWrap()
                            padding(12f, 8f, 12f, 8f)
                        }
                        vfor({ ctx.hotTags }) { tag: String ->
                            Text {
                                attr {
                                    margin(4f, 4f, 4f, 4f)
                                    padding(8f, 4f, 8f, 4f)
                                    borderRadius(16f)
                                    backgroundColor(Color(0xFFE8E8E8))
                                    fontSize(13f)
                                    color(Color(0xFF333333))
                                    text(tag)
                                }
                                event {
                                    click {
                                        ctx.selectedTag = tag
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