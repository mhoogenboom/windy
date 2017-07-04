package com.robinfinch.windy.app

import com.robinfinch.windy.ui.UiModule
import com.robinfinch.windy.ui.controller.ControllersModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(UiModule::class, ControllersModule::class))
interface AppComponent {

    fun inject(app: WindyApp)
}