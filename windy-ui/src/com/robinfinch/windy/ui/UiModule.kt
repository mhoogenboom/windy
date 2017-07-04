package com.robinfinch.windy.ui

import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

@Module
class UiModule {

    @Provides
    @Singleton
    fun providesTexts() = ResourceBundle.getBundle("com.robinfinch.windy.ui.texts")
}