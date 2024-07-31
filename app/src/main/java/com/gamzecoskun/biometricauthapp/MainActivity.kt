package com.gamzecoskun.biometricauthapp

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gamzecoskun.biometricauthapp.BiometricPromptManager.*
import com.gamzecoskun.biometricauthapp.BiometricPromptManager.BiometricResult.*
import com.gamzecoskun.biometricauthapp.ui.theme.BiometricAuthAppTheme

class MainActivity : AppCompatActivity() {

    private val promptManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BiometricAuthAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    val biometricResult by promptManager.promptResults.collectAsState(
                        initial = null
                    )
                    val enrollLauncher= rememberLauncherForActivityResult(
                        contract =ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            println("Activity result: $it ")
                        }
                    )

                    LaunchedEffect(biometricResult) {
                        if (biometricResult is AuthenticationNotSet ){
                            if (Build.VERSION.SDK_INT>=30){
                                val enroolIntent=Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                    putExtra(
                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                        Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                    )
                                }
                                enrollLauncher.launch(enroolIntent)
                            }
                        }

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DisplayImage()

                        Button(onClick = {
                            promptManager.showBiometricPrompt(
                                title = "Sample prompt",
                                description = "Sample prompt description"
                            )
                        }) {
                            
                            Text(text = "Authenticate")
                        }
                        biometricResult?.let { result ->
                            Text(
                                text = when (result) {
                                    is AuthenticationError -> {
                                        result.error
                                    }

                                    AuthenticaitonSuccess -> {
                                        "Authentication success"
                                    }

                                    AuthenticationFailed -> {
                                        "Authentication failed"
                                    }

                                    AuthenticationNotSet -> {
                                        "Authentication not set"
                                    }

                                    FeatureUnavailable -> {
                                        "Feature unavailable"
                                    }

                                    HardwareUnavailable -> {
                                        "Hardware Unavailable"
                                    }
                                }
                            )

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisplayImage() {
    val image: Painter = painterResource(id = R.drawable.finger)
    Image(
        painter = image,
        contentDescription = "My Image",
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BiometricAuthAppTheme {
        Greeting("Android")
        DisplayImage()
    }
}