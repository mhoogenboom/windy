package com.robinfinch.windy.ui.controller

import com.robinfinch.windy.api.WindyApi
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
    fun providesRemotePlayController(view: View, texts: ResourceBundle, api: WindyApi, db: Database)
            = RemotePlayController(view, texts, api, db)

    @Provides
    @Singleton
    fun providesInputGameController(view: View, texts: ResourceBundle, db: Database)
            = InputGameController(view, texts, db)

    @Provides
    @Singleton
    fun providesImportGamesController(view: View, texts: ResourceBundle, db: Database,
                                      replayGamesController: ReplayGamesController)
            = ImportGamesController(view, texts, db, replayGamesController)

    @Provides
    @Singleton
    fun providesReplayGamesController(view: View, texts: ResourceBundle, db: Database)
            = ReplayGamesController(view, texts, db)

    @Provides
    @Singleton
    fun providesInputExerciseController(view: View, texts: ResourceBundle, db: Database)
            = InputExerciseController(view, texts, db)

    @Provides
    @Singleton
    fun providesSolvesExerciseController(view: View, texts: ResourceBundle, db: Database)
            = SolvesExercisesController(view, texts, db)

}