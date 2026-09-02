package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vfor
import com.tencent.kuikly.core.module.NetworkModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.reactive.handler.observableList
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*
import com.tencent.kuikly.core.module.Module

data class WeatherDetailItem(
    val label: String,
    val value: String
)

@Page("WeatherPage")
internal class WeatherPage : Pager() {

    private var cityName by observable("")
    private var temperature by observable("")
    private var weatherDetails by observableList<WeatherDetailItem>()

    private lateinit var networkModule: NetworkModule

    override fun created() {
        super.created()
        networkModule = acquireModule(NetworkModule.MODULE_NAME)
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        networkModule.requestGet(
            "https://api.weather.com/current",
            JSONObject().apply {
                put("city", "Beijing")
            },
            { response, success, _ ->
                if (success) {
                    cityName = response.optString("cityName", "")
                    temperature = response.optString("temperature", "")
                    val details = mutableListOf<WeatherDetailItem>()
                    val detailsArray = response.optJSONArray("details")
                    if (detailsArray != null) {
                        for (i in 0 until detailsArray.length()) {
                            val item = detailsArray.optJSONObject(i)
                            if (item != null) {
                                details.add(
                                    WeatherDetailItem(
                                        label = item.optString("label", ""),
                                        value = item.optString("value", "")
                                    )
                                )
                            }
                        }
                    }
                    weatherDetails.clear()
                    weatherDetails.addAll(details)
                }
            }
        )
    }

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                }
                // 顶部区域：城市名 + 温度
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                    }
                    Text {
                        attr {
                            text(ctx.cityName)
                            fontSize(24f)
                            color(Color(0xFF333333))
                            fontWeightBold()
                        }
                    }
                    Text {
                        attr {
                            text(ctx.temperature)
                            fontSize(48f)
                            color(Color(0xFF333333))
                            fontWeightBold()
                            marginLeft(12f)
                        }
                    }
                }
                // 中间区域：天气图标
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                    }
                    Image {
                        attr {
                            size(120f, 120f)
                            src(ImageUri.pageAssets("weather_sunny"))
                        }
                    }
                }
                // 底部区域：风力/湿度等详情卡片列表
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                        padding(16f, 0f, 16f, 0f)
                    }
                    vfor({ ctx.weatherDetails }) { item ->
                        View {
                            attr {
                                width(pagerData.pageViewWidth - 32f)
                                height(60f)
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(12f)
                                marginBottom(10f)
                                flexDirectionRow()
                                alignItemsCenter()
                                padding(16f, 0f, 16f, 0f)
                            }
                            Text {
                                attr {
                                    text(item.label)
                                    fontSize(16f)
                                    color(Color(0xFF666666))
                                }
                            }
                            Text {
                                attr {
                                    text(item.value)
                                    fontSize(16f)
                                    color(Color(0xFF333333))
                                    fontWeightMedium()
                                    marginLeft(8f)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}