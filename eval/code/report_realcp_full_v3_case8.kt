package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexWrap
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

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    flex(1f)
                    paddingTop(pagerData.safeAreaInsets.top)
                    paddingBottom(pagerData.safeAreaInsets.bottom)
                    padding(0f, pagerData.safeAreaInsets.top, 0f, pagerData.safeAreaInsets.bottom)
                    backgroundColor(Color(0xFFF5F5F5))
                }
                Scroller {
                    attr {
                        flex(1f)
                    }
                    View {
                        attr {
                            marginTop(12f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                        Input {
                            attr {
                                height(44f)
                                borderRadius(22f)
                                backgroundColor(Color(0xFFFFFFFF))
                                marginLeft(16f)
                                marginRight(16f)
                                fontSize(16f)
                                placeholder("搜索")
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
                            flexDirection(FlexDirection.ROW)
                            flexWrap(FlexWrap.WRAP)
                            marginTop(20f)
                            marginLeft(16f)
                            marginRight(16f)
                        }
                        vfor({ ctx.hotTags }) { item ->
                            Text {
                                attr {
                                    marginLeft(12f)
                                    marginRight(12f)
                                    marginTop(6f)
                                    marginBottom(6f)
                                    borderRadius(16f)
                                    backgroundColor(Color(0xFFE8E8E8))
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                    text(item)
                                }
                                event {
                                    click { params ->
                                        ctx.searchText = item
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