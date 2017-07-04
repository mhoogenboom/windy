package com.robinfinch.windy.ui

import com.robinfinch.windy.ui.controller.View
import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

@Module
class UiModule {

    @Provides
    @Singleton
    fun providesTexts() = ResourceBundle.getBundle("com.robinfinch.windy.ui.texts")

    @Provides
    @Singleton
    fun providesView(texts: ResourceBundle): View = MainFrame(texts)
}