package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.layout.Center
import com.tencent.kuikly.core.views.*

@Page("WeatherPage")
internal class WeatherPage : Pager() {

    override fun body(): ViewBuilder {
        return {
            Scroller {
                attr {
                    size(pagerData.pageViewWidth, pagerData.pageViewHeight)
                }
                View {
                    attr {
                        flex(1f)
                        flexDirectionColumn()
                    }
                    // 顶部区域：城市名和温度
                    View {
                        attr {
                            flexDirectionColumn()
                            alignItemsCenter()
                            paddingTop(40f)
                            paddingBottom(20f)
                        }
                        Center {
                            Text {
                                attr {
                                    text("北京")
                                    fontSize(24f)
                                    fontWeightBold()
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                        Center {
                            Text {
                                attr {
                                    text("25°C")
                                    fontSize(48f)
                                    fontWeightLight()
                                    color(Color(0xFF333333))
                                    marginTop(8f)
                                }
                            }
                        }
                    }
                    // 中间区域：天气图标
                    Center {
                        attr {
                            flex(1f)
                        }
                        Image {
                            attr {
                                size(120f, 120f)
                                src(ImageUri.pageAssets("weather_sunny"))
                            }
                        }
                    }
                    // 底部区域：信息卡片
                    View {
                        attr {
                            flexDirectionColumn()
                            paddingLeft(16f)
                            paddingRight(16f)
                            paddingBottom(30f)
                        }
                        // 卡片1：湿度
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(12f)
                                padding(16f)
                                marginBottom(12f)
                            }
                            Text {
                                attr {
                                    text("湿度")
                                    fontSize(16f)
                                    color(Color(0xFF666666))
                                }
                            }
                            View {
                                attr {
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text("65%")
                                    fontSize(16f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                        // 卡片2：风速
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(12f)
                                padding(16f)
                                marginBottom(12f)
                            }
                            Text {
                                attr {
                                    text("风速")
                                    fontSize(16f)
                                    color(Color(0xFF666666))
                                }
                            }
                            View {
                                attr {
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text("3级")
                                    fontSize(16f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                        // 卡片3：空气质量
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(12f)
                                padding(16f)
                                marginBottom(12f)
                            }
                            Text {
                                attr {
                                    text("空气质量")
                                    fontSize(16f)
                                    color(Color(0xFF666666))
                                }
                            }
                            View {
                                attr {
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text("良好")
                                    fontSize(16f)
                                    fontWeightMedium()
                                    color(Color(0xFF333333))
                                }
                            }
                        }
                        // 卡片4：紫外线
                        View {
                            attr {
                                flexDirectionRow()
                                alignItemsCenter()
                                backgroundColor(Color(0xFFF5F5F5))
                                borderRadius(12f)
                                padding(16f)
                            }
                            Text {
                                attr {
                                    text("紫外线")
                                    fontSize(16f)
                                    color(Color(0xFF666666))
                                }
                            }
                            View {
                                attr {
                                    flex(1f)
                                }
                            }
                            Text {
                                attr {
                                    text("中等")
                                    fontSize(16f)
                                    fontWeightMedium()
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