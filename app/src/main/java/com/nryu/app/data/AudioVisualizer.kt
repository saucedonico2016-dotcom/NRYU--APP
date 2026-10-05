package com.nryu.app.data

import android.media.audiofx.Visualizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioVisualizer {
    private var visualizer: Visualizer? = null
    private val _amplitude = MutableStateFlow(FloatArray(0))
    val amplitude: StateFlow<FloatArray> = _amplitude.asStateFlow()

    fun start(audioSessionId: Int) {
        visualizer = Visualizer(audioSessionId).apply {
            captureSize = Visualizer.getCaptureSizeRange()[1]
            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(v: Visualizer, waveform: ByteArray, samplingRate: Int) {
                    // We use waveform for simple amplitude
                    val floatArray = FloatArray(waveform.size) { i ->
                        (waveform[i].toInt() and 0xFF).toFloat() / 255f
                    }
                    _amplitude.value = floatArray
                }

                override fun onFftDataCapture(v: Visualizer, fft: ByteArray, samplingRate: Int) {
                    // FFT for frequency analysis
                    val magnitudes = FloatArray(fft.size / 2)
                    for (i in 0 until fft.size / 2) {
                        val real = fft[i * 2].toInt()
                        val imag = fft[i * 2 + 1].toInt()
                        magnitudes[i] = Math.sqrt((real * real + imag * imag).toDouble()).toFloat()
                    }
                    _amplitude.value = magnitudes
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false)
            enabled = true
        }
    }

    fun stop() {
        visualizer?.enabled = false
        visualizer?.release()
        visualizer = null
    }
}
