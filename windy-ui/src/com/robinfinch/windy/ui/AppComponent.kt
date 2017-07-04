package com.robinfinch.windy.ui

import dagger.Component

@Component(modules = arrayOf(UiModule::class))
interface AppComponent {

    fun inject(app: WindyApp)
}