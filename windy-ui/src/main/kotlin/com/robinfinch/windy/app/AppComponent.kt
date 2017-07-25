package com.robinfinch.windy.app

import com.robinfinch.windy.api.ApiModule
import com.robinfinch.windy.api.ApiModule_ProvidesApiFactory
import com.robinfinch.windy.ui.UiModule
import com.robinfinch.windy.ui.controller.ControllersModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(UiModule::class, ControllersModule::class, ApiModule::class))
interface AppComponent {

    fun inject(app: WindyApp)
}