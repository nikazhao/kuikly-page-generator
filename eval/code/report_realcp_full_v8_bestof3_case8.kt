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
                    flex(1f)
                }
                Scroller {
                    attr {
                        flex(1f)
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 48f)
                            marginTop(16f)
                            marginLeft(16f)
                            marginRight(16f)
                            borderRadius(24f)
                            backgroundColor(Color(0xFFF5F5F5))
                            flexDirectionRow()
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text("🔍")
                                fontSize(18f)
                                marginLeft(16f)
                                marginRight(8f)
                            }
                        }
                        Input {
                            attr {
                                flex(1f)
                                height(48f)
                                fontSize(14f)
                                color(Color(0xFF333333))
                                placeholder("搜索热门内容")
                                placeholderColor(Color(0xFF999999))
                            }
                            event {
                                textDidChange { params ->
                                    ctx.searchText = params.text
                                }
                            }
                        }
                    }
                    View {
                        attr {
                            size(pagerData.pageViewWidth, 0f)
                            marginTop(20f)
                            marginLeft(16f)
                            marginRight(16f)
                            flexDirectionRow()
                            flexWrapWrap()
                        }
                        vfor({ ctx.hotTags }) { item ->
                            View {
                                attr {
                                    borderRadius(16f)
                                    backgroundColor(Color(0xFFF0F0F0))
                                    padding(8f, 4f, 8f, 4f)
                                    marginRight(8f)
                                    marginBottom(8f)
                                }
                                event {
                                    click {
                                        val tag = item.toString()
                                        ctx.searchText = tag
                                        val router = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                        router.openPage("SearchResultPage", JSONObject().apply {
                                            put("keyword", tag)
                                        })
                                    }
                                }
                                Text {
                                    attr {
                                        text(item.toString())
                                        fontSize(14f)
                                        color(Color(0xFF666666))
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