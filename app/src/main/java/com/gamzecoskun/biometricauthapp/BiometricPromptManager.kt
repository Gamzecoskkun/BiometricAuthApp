package com.gamzecoskun.biometricauthapp


import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


/***
 * Created on 24.07.2024
 *@author Gamze Coşkun
 */
class BiometricPromptManager(
    private val activity: AppCompatActivity
) {
    private val resultChannel= Channel<BiometricResult>()
    val promptResults=resultChannel.receiveAsFlow()

    //@SuppressLint("WrongConstant")
    fun showBiometricPrompt(
        title: String,
        description: String,
    ) {
        val manager = BiometricManager.from(activity)
        //  val authenticators=BIOMETRIC_STRONG or  DEVICE_CREDENTIAL //Can choose biometric or device but android level is limited

        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else BIOMETRIC_STRONG

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
        .setAllowedAuthenticators(authenticators)


        if (Build.VERSION.SDK_INT < 30) {
            promptInfo.setNegativeButtonText("Cancel")
        }

        when(manager.canAuthenticate(authenticators)){
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE->{
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE->{
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED->{
                resultChannel.trySend(BiometricResult.AuthenticationNotSet)
                return
            }
            else->Unit
        }

        val prompt=BiometricPrompt(
            activity,
            object:BiometricPrompt.AuthenticationCallback(){

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    resultChannel.trySend(BiometricResult.AuthenticaitonSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }

            }
        )
        prompt.authenticate(promptInfo.build())


    }



    sealed interface BiometricResult {
        data object HardwareUnavailable : BiometricResult
        data object FeatureUnavailable : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticaitonSuccess : BiometricResult
        data object AuthenticationNotSet : BiometricResult
    }
}