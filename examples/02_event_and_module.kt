package com.tencent.kuikly.demo.pages

import com.tencent.kuikly.compose.material3.Scaffold
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.pager.Pager
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.views.Scroller
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.demo.pages.base.BasePager

@Page("TBTest")
internal class TBTestPage : BasePager() {

    var moduleRetureValue by observable("点击显示ArkTS层 Module返回值")
    var moduleRetureValue2 by observable("点击显示C++层 Module返回值 ")

    override fun body(): ViewBuilder {
        val ctx = this
        return {
            attr {
                allCenter()
            }
            View {
                attr {
                    size(getPager().pageData.pageViewWidth, getPager().pageData.pageViewHeight)
                }
                Text {
                    attr {
                        marginLeft(10f)
                        text(ctx.moduleRetureValue)
                    }
                    event {
                        click {
                            ctx.moduleRetureValue = getPager().acquireModule<MyLogModule>("KRMyLogModule").test()
                        }
                    }
                }
                Text {
                    attr {
                        marginTop(30f)
                        marginLeft(10f)
                        text(ctx.moduleRetureValue2)
                        color(Color.BLUE)
                    }
                    event {
                        click {
                            ctx.moduleRetureValue2 = getPager().acquireModule<LogTestModule>("KRLogTestModule").test()
                        }
                    }
                }
            }
        }
    }

    override fun createExternalModules(): Map<String, Module>? {
        return mapOf(
            "KRMyLogModule" to MyLogModule(),
            "KRLogTestModule" to LogTestModule()
        )
    }
}
