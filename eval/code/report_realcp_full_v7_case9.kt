package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.directives.vforLazy
import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

@Page("DataDashboardPage")
internal class DataDashboardPage : Pager() {

    private var card1Value by observable("0")
    private var card2Value by observable("0")
    private var card3Value by observable("0")
    private var progressValue by observable(0f)
    private var itemList by observableList<String>()
    private lateinit var spModule: SharedPreferencesModule
    private lateinit var networkModule: NetworkModule

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        networkModule = acquireModule(NetworkModule.MODULE_NAME)

        card1Value = spModule.getString("card1Value")
        card2Value = spModule.getString("card2Value")
        card3Value = spModule.getString("card3Value")
        progressValue = (spModule.getInt("progressValue") ?: 0).toFloat()
        val cachedList = spModule.getString("itemList")
        if (cachedList.isNotEmpty()) {
            val list = cachedList.split(",").toMutableList()
            itemList.clear()
            itemList.addAll(list)
        }

        networkModule.requestGet(
            "https://api.example.com/dashboard",
            JSONObject(),
            object : CallbackFn {
                override fun onResult(result: Any?) {
                    val json = result as? JSONObject ?: return
                    card1Value = json.optString("card1", "0")
                    card2Value = json.optString("card2", "0")
                    card3Value = json.optString("card3", "0")
                    progressValue = json.optDouble("progress", 0.0).toFloat()
                    val list = json.optJSONArray("items")
                    if (list != null) {
                        val newList = (0 until list.length()).map { list.optString(it) }
                        itemList.clear()
                        itemList.addAll(newList)
                    }

                    spModule.setString("card1Value", card1Value)
                    spModule.setString("card2Value", card2Value)
                    spModule.setString("card3Value", card3Value)
                    spModule.setInt("progressValue", progressValue.toInt())
                    spModule.setString("itemList", itemList.joinToString(","))
                }
            }
        )
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    width(pagerData.pageViewWidth)
                    height(pagerData.pageViewHeight)
                    flexDirectionColumn()
                    backgroundColor(Color(0xFFF5F5F5))
                }
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingTop(16f)
                        paddingBottom(16f)
                        paddingLeft(16f)
                        paddingRight(16f)
                        flexDirectionRow()
                        alignItemsCenter()
                    }
                    View {
                        attr {
                            flex(1f)
                            height(80f)
                            marginRight(8f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFFFFFFF))
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("总用户")
                                fontSize(14f)
                                color(Color(0xFF666666))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card1Value)
                                fontSize(24f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            height(80f)
                            marginLeft(8f)
                            marginRight(8f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFFFFFFF))
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("活跃用户")
                                fontSize(14f)
                                color(Color(0xFF666666))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card2Value)
                                fontSize(24f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                    View {
                        attr {
                            flex(1f)
                            height(80f)
                            marginLeft(8f)
                            borderRadius(12f)
                            backgroundColor(Color(0xFFFFFFFF))
                            alignItemsCenter()
                            justifyContentCenter()
                        }
                        Text {
                            attr {
                                text("收入")
                                fontSize(14f)
                                color(Color(0xFF666666))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.card3Value)
                                fontSize(24f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                marginTop(4f)
                            }
                        }
                    }
                }
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        paddingLeft(16f)
                        paddingRight(16f)
                        paddingBottom(16f)
                    }
                    Text {
                        attr {
                            text("任务完成进度")
                            fontSize(16f)
                            fontWeightMedium()
                            color(Color(0xFF333333))
                            marginBottom(8f)
                        }
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth - 32f)
                            height(20f)
                            borderRadius(10f)
                            backgroundColor(Color(0xFFE0E0E0))
                        }
                        View {
                            attr {
                                width((pagerData.pageViewWidth - 32f) * ctx.progressValue)
                                height(20f)
                                borderRadius(10f)
                                backgroundColor(Color(0xFF4A90D9))
                            }
                        }
                    }
                }
                List {
                    attr {
                        flex(1f)
                        width(pagerData.pageViewWidth)
                        paddingLeft(16f)
                        paddingRight(16f)
                    }
                    vforLazy({ ctx.itemList }) { item: String, index: Int, count: Int ->
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 32f)
                                height(56f)
                                marginBottom(8f)
                                borderRadius(8f)
                                backgroundColor(Color(0xFFFFFFFF))
                                alignItemsCenter()
                                flexDirectionRow()
                                paddingLeft(16f)
                                paddingRight(16f)
                            }
                            Text {
                                attr {
                                    text("${index + 1}")
                                    fontSize(16f)
                                    fontWeightMedium()
                                    color(Color(0xFF4A90D9))
                                    marginRight(12f)
                                }
                            }
                            Text {
                                attr {
                                    text(item)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}