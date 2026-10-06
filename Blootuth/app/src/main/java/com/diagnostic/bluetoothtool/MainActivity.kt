package com.diagnostic.bluetoothtool

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.diagnostic.bluetoothtool.model.ScannedDevice
import com.diagnostic.bluetoothtool.ui.screens.DeviceDetailScreen
import com.diagnostic.bluetoothtool.ui.screens.DevicePortConfigScreen
import com.diagnostic.bluetoothtool.ui.screens.FireLensGeminiScreen
import com.diagnostic.bluetoothtool.ui.screens.HomeScreen
import com.diagnostic.bluetoothtool.ui.theme.BluetoothDiagnosticTheme
import com.diagnostic.bluetoothtool.ui.theme.DarkBackground
import com.diagnostic.bluetoothtool.viewmodel.MainViewModel

enum class AppScreenState {
    HOME,
    DEVICE_DETAIL,
    DEVICE_PORTS,
    FIRE_LENS_GEMINI
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val btScanGranted = permissions[Manifest.permission.BLUETOOTH_SCAN] ?: true
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: true
        if (btScanGranted || locationGranted) {
            viewModel.startScanning()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BluetoothDiagnosticTheme {
                LaunchedEffect(Unit) {
                    checkAndRequestPermissions()
                }
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            viewModel.startScanning()
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    var screenState by remember { mutableStateOf(AppScreenState.HOME) }
    var viewingDevice by remember { mutableStateOf<ScannedDevice?>(null) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(innerPadding)
        ) {
            when (screenState) {
                AppScreenState.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToDeviceDetail = { dev ->
                            viewingDevice = dev
                            screenState = AppScreenState.DEVICE_DETAIL
                        },
                        onNavigateToGemini = {
                            screenState = AppScreenState.FIRE_LENS_GEMINI
                        }
                    )
                }
                AppScreenState.DEVICE_DETAIL -> {
                    viewingDevice?.let { dev ->
                        DeviceDetailScreen(
                            device = dev,
                            viewModel = viewModel,
                            onBack = {
                                screenState = AppScreenState.HOME
                            },
                            onNavigateToPorts = {
                                screenState = AppScreenState.DEVICE_PORTS
                            },
                            onNavigateToGemini = {
                                screenState = AppScreenState.FIRE_LENS_GEMINI
                            }
                        )
                    } ?: run {
                        screenState = AppScreenState.HOME
                    }
                }
                AppScreenState.DEVICE_PORTS -> {
                    viewingDevice?.let { dev ->
                        DevicePortConfigScreen(
                            device = dev,
                            viewModel = viewModel,
                            onBack = {
                                screenState = AppScreenState.DEVICE_DETAIL
                            }
                        )
                    } ?: run {
                        screenState = AppScreenState.HOME
                    }
                }
                AppScreenState.FIRE_LENS_GEMINI -> {
                    FireLensGeminiScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            screenState = if (viewingDevice != null) AppScreenState.DEVICE_DETAIL else AppScreenState.HOME
                        }
                    )
                }
            }
        }
    }
}
