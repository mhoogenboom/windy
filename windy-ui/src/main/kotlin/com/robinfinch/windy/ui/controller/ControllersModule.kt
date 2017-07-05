package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.db.Database
import dagger.Module
import dagger.Provides
import java.io.File
import java.util.*
import javax.inject.Singleton

@Module
class ControllersModule {

    @Provides
    @Singleton
    fun providesDatabase() = Database(File("data"))

    @Provides
    @Singleton
    fun providesLocalPlayController(view: View, texts: ResourceBundle, db: Database)
            = LocalPlayController(view, texts, db)

    @Provides
    @Singleton
    fun providesInputGameController(view: View, texts: ResourceBundle, db: Database)
            = InputGameController(view, texts, db)

    @Provides
    @Singleton
    fun providesImportGamesController(view: View, texts: ResourceBundle, db: Database)
            = ImportGamesController(view, texts, db)

}