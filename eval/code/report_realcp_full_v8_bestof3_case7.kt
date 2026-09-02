package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*

@Page("WeatherPage")
internal class WeatherPage : Pager() {

    private var cityName by observable("")
    private var temperature by observable("")
    private var weatherIcon by observable("")
    private var windPower by observable("")
    private var humidity by observable("")
    private var isLoading by observable(true)

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                    flexDirectionColumn()
                }
                // 顶部区域：城市名和温度
                Center {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                    }
                    View {
                        attr {
                            flexDirectionColumn()
                            alignItemsCenter()
                        }
                        Text {
                            attr {
                                text(ctx.cityName.ifEmpty { "北京" })
                                fontSize(24f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                            }
                        }
                        Text {
                            attr {
                                text(ctx.temperature.ifEmpty { "25°C" })
                                fontSize(48f)
                                fontWeightBold()
                                color(Color(0xFF333333))
                                marginTop(8f)
                            }
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
                // 底部区域：多个卡片
                View {
                    attr {
                        width(pagerData.pageViewWidth)
                        flex(1f)
                        padding(16f, 0f, 16f, 16f)
                    }
                    View {
                        attr {
                            width(pagerData.pageViewWidth)
                            flexDirectionRow()
                            justifyContentSpaceAround()
                        }
                        // 卡片1：风力
                        View {
                            attr {
                                size(100f, 100f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF5F5F5))
                            }
                            Center {
                                Text {
                                    attr {
                                        text("风力")
                                        fontSize(14f)
                                        color(Color(0xFF999999))
                                    }
                                }
                                Text {
                                    attr {
                                        text(ctx.windPower.ifEmpty { "3级" })
                                        fontSize(20f)
                                        fontWeightBold()
                                        color(Color(0xFF333333))
                                        marginTop(4f)
                                    }
                                }
                            }
                        }
                        // 卡片2：湿度
                        View {
                            attr {
                                size(100f, 100f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF5F5F5))
                            }
                            Center {
                                Text {
                                    attr {
                                        text("湿度")
                                        fontSize(14f)
                                        color(Color(0xFF999999))
                                    }
                                }
                                Text {
                                    attr {
                                        text(ctx.humidity.ifEmpty { "65%" })
                                        fontSize(20f)
                                        fontWeightBold()
                                        color(Color(0xFF333333))
                                        marginTop(4f)
                                    }
                                }
                            }
                        }
                        // 卡片3：空气质量
                        View {
                            attr {
                                size(100f, 100f)
                                borderRadius(12f)
                                backgroundColor(Color(0xFFF5F5F5))
                            }
                            Center {
                                Text {
                                    attr {
                                        text("空气质量")
                                        fontSize(14f)
                                        color(Color(0xFF999999))
                                    }
                                }
                                Text {
                                    attr {
                                        text("良")
                                        fontSize(20f)
                                        fontWeightBold()
                                        color(Color(0xFF333333))
                                        marginTop(4f)
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