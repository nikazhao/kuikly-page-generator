package com.tencent.kuikly.demo.pages.app

import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.Animation
import com.tencent.kuikly.core.base.Border
import com.tencent.kuikly.core.base.BorderStyle
import com.tencent.kuikly.core.base.Color
import com.tencent.kuikly.core.base.Scale
import com.tencent.kuikly.core.base.ViewBuilder
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.directives.vif
import com.tencent.kuikly.core.module.NotifyModule
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.module.SharedPreferencesModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.reactive.handler.observable
import com.tencent.kuikly.core.timer.setTimeout
import com.tencent.kuikly.core.views.ActivityIndicator
import com.tencent.kuikly.core.views.Image
import com.tencent.kuikly.core.views.List
import com.tencent.kuikly.core.views.Modal
import com.tencent.kuikly.core.views.Text
import com.tencent.kuikly.core.views.TransitionType
import com.tencent.kuikly.core.views.TransitionView
import com.tencent.kuikly.core.views.View
import com.tencent.kuikly.demo.pages.app.lang.MultiLingualPager
import com.tencent.kuikly.demo.pages.app.lang.LangManager
import com.tencent.kuikly.demo.pages.app.theme.ThemeManager

@Page("AppSettingPage")
internal class AppSettingPage : MultiLingualPager() {

    private var theme by observable(ThemeManager.getTheme())
    private var lang by observable(LangManager.getCurrentLanguage())
    private lateinit var settingLangHint: String
    private var showModal by observable(false)

    private lateinit var spModule: SharedPreferencesModule
    private lateinit var notifyModule: NotifyModule

    override fun created() {
        super.created()
        spModule = acquireModule(SharedPreferencesModule.MODULE_NAME)
        notifyModule = acquireModule(NotifyModule.MODULE_NAME)
    }

    private fun topNavBar(): ViewBuilder {
        val ctx = this
        return {
            View {
                attr {
                    paddingTop(ctx.pagerData.statusBarHeight)
                    backgroundColor(ctx.theme.colors.topBarBackground)
                }
                View {
                    attr {
                        height(44f)
                        allCenter()
                    }

                    Text {
                        attr {
                            text(ctx.resStrings.setting)
                            color(ctx.theme.colors.topBarTextFocused)
                            fontSize(17f)
                            fontWeightSemiBold()
                        }
                    }
                }

                Image {
                    attr {
                        absolutePosition(12f + getPager().pageData.statusBarHeight, 12f, 12f, 12f)
                        size(10f, 17f)
                        src("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAASBAMAAAB/WzlGAAAAElBMVEUAAAAAAAAAAAAAAAAAAAAAAADgKxmiAAAABXRSTlMAIN/PELVZAGcAAAAkSURBVAjXYwABQTDJqCQAooSCHUAcVROCHBiFECTMhVoEtRYA6UMHzQlOjQIAAAAASUVORK5CYII=")
                        tintColor(ctx.theme.colors.topBarTextFocused)
                    }
                    event {
                        click {
                            getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
