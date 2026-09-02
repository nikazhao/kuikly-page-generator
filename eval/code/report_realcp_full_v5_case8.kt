package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexWrap
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Input
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("SearchPage")
internal class SearchPage : Pager() {

    private var searchText by observable("")
    private var hotTags by observableList<String>()

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirection(FlexDirection.COLUMN)
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
                            paddingLeft(16f)
                            paddingRight(16f)
                        }
                        event {
                            textDidChange { params ->
                                ctx.searchText = params.text
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
                            flexWrap(FlexWrap.WRAP)
                            flexDirection(FlexDirection.ROW)
                            paddingLeft(16f)
                            paddingRight(16f)
                            paddingTop(16f)
                        }
                        vfor({ ctx.hotTags }) { tag ->
                            View {
                                attr {
                                    height(36f)
                                    marginRight(10f)
                                    marginBottom(10f)
                                    borderRadius(18f)
                                    backgroundColor(Color(0xFFF0F0F0))
                                    paddingLeft(16f)
                                    paddingRight(16f)
                                    alignItemsCenter()
                                    justifyContentCenter()
                                }
                                Text {
                                    attr {
                                        text(tag)
                                        fontSize(14f)
                                        color(Color(0xFF333333))
                                    }
                                }
                                event {
                                    click {
                                        val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        router?.openPage("SearchResultPage", JSONObject().apply {
                                            put("keyword", tag)
                                        })
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