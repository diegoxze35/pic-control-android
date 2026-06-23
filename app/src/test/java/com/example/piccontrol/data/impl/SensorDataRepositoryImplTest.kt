package com.example.piccontrol.data.impl

import app.cash.turbine.test
import com.example.piccontrol.data.BluetoothController
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SensorDataRepositoryImplTest {

    // Creamos un "doble" (Mock) del controlador. No usa hardware real.
    private val mockBluetoothController: BluetoothController = mockk()
    
    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Test
    fun `when bluetooth sends raw data, repository parses it and emits correct SensorData`() = runTest(testDispatcher) {
        val fakeIncomingData = MutableSharedFlow<String>()
        // Le decimos al mock: "Cuando alguien te pida incomingData, entrégale mi flow falso"
        every { mockBluetoothController.incomingData } returns fakeIncomingData
        val repository = SensorDataRepositoryImpl(
            bluetoothController = mockBluetoothController,
            coroutineScope = testScope 
        )
        // Turbine para probar Flows
        repository.sensorData.test {
            // El flow siempre emite un valor inicial
            val initialState = awaitItem()
            assertEquals(0, initialState.p1)

            //Emitimos un string "sucio" simulando que viene del PIC
            fakeIncomingData.emit("P1:100,P2:200\n")
            //Esperamos que el repositorio parsee eso y emita el nuevo estado
            val newState = awaitItem()
            assertEquals(100, newState.p1)
            assertEquals(200, newState.p2)
            assertEquals(0, newState.p3) // No mandamos P3, debe seguir en 0
        }
    }
}
