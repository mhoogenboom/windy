package com.robinfinch.windy.api

import com.robinfinch.windy.core.game.RemoteGameStatus
import com.robinfinch.windy.ui.edt
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection

class StatusChecker(val api: WindyApi) {

    companion object {
        private const val MAX_RETRIES = 3
    }

    fun checkStatus(gameId: String,
                    onStatusChecked: (RemoteGameStatus) -> Unit,
                    onStatusCheckFailed: (String?) -> Unit) {

        check(gameId, onStatusChecked, onStatusCheckFailed, MAX_RETRIES)
    }

    private fun check(gameId: String,
                      onStatusChecked: (RemoteGameStatus) -> Unit,
                      onStatusCheckFailed: (String?) -> Unit,
                      retriesLeft: Int) {

        val retry = if (retriesLeft > 0)
            { e: Throwable ->
                Thread.sleep(600);

                check(gameId, onStatusChecked, onStatusCheckFailed, retriesLeft - 1)
            }
        else
            { e: Throwable ->
                onStatusCheckFailed(e.message)
            }

        api.getStatus(gameId)
                .subscribeOn(Schedulers.io())
                .observeOn(edt())
                .subscribe(
                        ifCodeElse<RemoteGameStatus>(HttpURLConnection.HTTP_OK,
                                onStatusChecked,
                                onStatusCheckFailed),
                        retry
                )
    }
}