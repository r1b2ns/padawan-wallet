/*
 * Copyright 2020-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package com.goldenraven.padawanwallet.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.goldenraven.padawanwallet.domain.bitcoin.WalletRepository
import com.goldenraven.padawanwallet.domain.bitcoin.Wallet
import com.goldenraven.padawanwallet.presentation.navigation.IntroNavigation
import com.goldenraven.padawanwallet.presentation.theme.PadawanTheme
import com.goldenraven.padawanwallet.presentation.navigation.HomeNavigation
import com.goldenraven.padawanwallet.presentation.ui.screens.settings.SupportedLanguage
import com.goldenraven.padawanwallet.presentation.ui.screens.settings.getSupportedLanguageCode
import com.goldenraven.padawanwallet.utils.SnackbarLevel
import com.goldenraven.padawanwallet.utils.fireSnackbar
import java.util.Locale

private const val TAG = "PadawanActivity"

class PadawanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // ask the repository if a wallet already exists
        // if so, load it and launch into wallet activity, otherwise go to intro
        if (WalletRepository.doesWalletExist()) {
            Wallet.loadWallet()
            Log.i(TAG, "Wallet already exists!")

            setContent {
                PadawanTheme {
                    HomeNavigation()
                }
            }
        } else {
            // this function is used in composables that navigate to the wallet activity
            // to ensure we destroy the intro activity once the wallet starts
            val onBuildWalletButtonClicked : (WalletCreateType) -> Unit = { walletCreateType ->
                try {
                    // load up a wallet either from scratch or using a BIP39 recovery phrase
                    when (walletCreateType) {
                        // if we create a wallet from scratch we don't need a recovery phrase
                        is WalletCreateType.FROMSCRATCH -> Wallet.createWallet()
                        is WalletCreateType.RECOVER -> Wallet.recoverWallet(walletCreateType.recoveryPhrase)
                    }

                    setContent {
                        PadawanTheme {
                            HomeNavigation()
                        }
                    }
                } catch (e: Throwable) {
                    Log.i(TAG, "Could not build wallet: $e")
                    fireSnackbar(
                        view = findViewById(android.R.id.content),
                        level = SnackbarLevel.ERROR,
                        message = "Error: $e"
                    )
                }
            }

            setContent {
                PadawanTheme {
                    IntroNavigation(onBuildWalletButtonClicked)
                }
            }
        }
    }

    // TODO: This feels hacky but the AppCompatDelegate.getApplicationLocales() API returns an
    //       an empty list if it hasn't been set manually. We cannot set it directly inside
    //       the onCreate() method, and so we run it here.
    // TODO: This can be cleaned up. The behaviour between API 33+ and > 33 are different, and not
    //       handled optimally by this code.
    // Languages: We check if the language has been manually set in the app
    // and if it has not we use the system default language.
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        setLanguage()
    }
}

fun setLanguage() {
    val localeListCompat: LocaleListCompat = AppCompatDelegate.getApplicationLocales()
    Log.i(TAG, "Current locale list compat is: $localeListCompat")

    if (localeListCompat.isEmpty) {
        Log.i(TAG, "Current LocaleListCompat is empty, we're calling AppCompatDelegate.setApplicationLocales()")
        val defaultSystemLocale = Locale.getDefault().language

        if (defaultSystemLocale.contains("es")) {
            Log.i(TAG, "Default system locale is Spanish")
            val languageCode: String = getSupportedLanguageCode(SupportedLanguage.SPANISH)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        } else if (defaultSystemLocale.contains("pt")) {
            Log.i(TAG, "Default system locale is Portuguese")
            val languageCode: String = getSupportedLanguageCode(SupportedLanguage.PORTUGUESE)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        } else {
            Log.i(TAG, "Default system locale is neither Spanish nor Portuguese, defaulting to English")
            val languageCode: String = getSupportedLanguageCode(SupportedLanguage.ENGLISH)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    } else {
        Log.i(TAG, "Current language in AppCompatDelegate was already set to: $localeListCompat")
    }
}

sealed class WalletCreateType {
    object FROMSCRATCH : WalletCreateType()
    class RECOVER(val recoveryPhrase: String) : WalletCreateType()
}
