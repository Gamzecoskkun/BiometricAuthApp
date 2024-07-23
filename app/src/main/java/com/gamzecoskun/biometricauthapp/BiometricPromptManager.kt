package com.gamzecoskun.biometricauthapp

import androidx.biometric.BiometricManager


/***
 * Created on 24.07.2024
 *@author Gamze Coşkun
 */
class BiometricPromptManager {
    private val activity:Activity
    fun showBiometricPrompt(
        title: String,
        description: String,
    ) {
        val manager = BiometricManager.from()
    }
}