package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
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
                }
                Scroller {
                    attr {
                        flex(1f)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            paddingTop(12f)
                            paddingLeft(16f)
                            paddingRight(16f)
                        }
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 32f)
                                height(40f)
                                borderRadius(20f)
                                backgroundColor(Color(0xFFF5F5F5))
                                paddingLeft(12f)
                                paddingRight(12f)
                            }
                            Input {
                                attr {
                                    width(pagerData.pageViewWidth - 56f)
                                    height(40f)
                                    fontSize(14f)
                                    placeholder("搜索热门标签")
                                }
                                event {
                                    textDidChange { params ->
                                        ctx.searchText = params.text
                                    }
                                }
                            }
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            paddingTop(16f)
                            paddingLeft(16f)
                            paddingRight(16f)
                            flexWrapWrap()
                            flexDirectionRow()
                            alignItemsFlexStart()
                        }
                        vfor({ ctx.hotTags }) { tag ->
                            View {
                                attr {
                                    marginRight(8f)
                                    marginBottom(8f)
                                    paddingLeft(12f)
                                    paddingRight(12f)
                                    paddingTop(6f)
                                    paddingBottom(6f)
                                    borderRadius(16f)
                                    backgroundColor(Color(0xFFF0F0F0))
                                }
                                Text {
                                    attr {
                                        text(tag.toString())
                                        fontSize(14f)
                                        color(Color(0xFF333333))
                                    }
                                }
                                event {
                                    click {
                                        ctx.searchText = tag.toString()
                                        val router = getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        router?.openPage("SearchResultPage", JSONObject().apply {
                                            put("keyword", tag.toString())
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