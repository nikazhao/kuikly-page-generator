package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.layout.FlexDirection
import com.tencent.kuikly.core.layout.FlexWrap
import com.tencent.kuikly.core.module.Module
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
                Input {
                    attr {
                        marginTop(12f)
                        marginLeft(16f)
                        marginRight(16f)
                        height(44f)
                        borderRadius(22f)
                        backgroundColor(Color(0xFFFFFFFF))
                        placeholder("搜索热门标签")
                        fontSize(14f)
                        marginLeft(16f)
                        marginRight(16f)
                    }
                    event {
                        textDidChange { params ->
                            ctx.searchText = params.text
                        }
                    }
                }
                Scroller {
                    attr {
                        flex(1f)
                        marginTop(12f)
                    }
                    View {
                        attr {
                            flexWrap(FlexWrap.WRAP)
                            marginLeft(12f)
                            marginRight(12f)
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("热门搜索")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("Kuikly")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "Kuikly"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
                                        put("keyword", tag)
                                    })
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("跨平台")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "跨平台"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
                                        put("keyword", tag)
                                    })
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("Kotlin")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "Kotlin"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
                                        put("keyword", tag)
                                    })
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("Android")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "Android"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
                                        put("keyword", tag)
                                    })
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("iOS")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "iOS"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
                                        put("keyword", tag)
                                    })
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("鸿蒙")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "鸿蒙"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
                                        put("keyword", tag)
                                    })
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("H5")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "H5"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
                                        put("keyword", tag)
                                    })
                                }
                            }
                        }
                        View {
                            attr {
                                margin(6f)
                                padding(8f, 10f, 8f, 10f)
                                borderRadius(16f)
                                backgroundColor(Color(0xFFFFFFFF))
                            }
                            Text {
                                attr {
                                    text("小程序")
                                    fontSize(14f)
                                    color(Color(0xFF333333))
                                }
                            }
                            event {
                                click { params ->
                                    val tag = "小程序"
                                    ctx.searchText = tag
                                    val routerModule = ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
                                    routerModule.openPage("SearchResultPage", JSONObject().apply {
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