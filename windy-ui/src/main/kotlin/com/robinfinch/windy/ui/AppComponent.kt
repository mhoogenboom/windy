package com.robinfinch.windy.ui

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(UiModule::class))
interface AppComponent {

    fun inject(app: WindyApp)
}