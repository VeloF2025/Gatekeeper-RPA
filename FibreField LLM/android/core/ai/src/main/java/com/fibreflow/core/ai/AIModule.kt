package com.fibreflow.core.ai

import android.content.Context
import com.fibreflow.core.ai.inference.InferenceEngine
import com.fibreflow.core.ai.llm.LLMManager
import com.fibreflow.core.ai.llm.Phi35Mini极M
import com.fibreflow.core.ai.vision.BarcodeScanner
import com.fibreflow.core.ai.vision.ONTLightDetector
import com.fibreflow.core.ai.vision.PhotoQualityAnalyzer
import com.fibreflow.core.ai.vision.TextExtractor
import com.fibreflow.core.ai.voice.SpeechRecognizer
import com.fibreflow.core.ai.voice.TextToSpeech
import com.fibreflow.core.ai.voice.VoiceCommandProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt Module for AI component dependency injection
 *
 * Provides singleton instances of AI components including:
 * - InferenceEngine for general AI inference management
 * - Phi35MiniLLM for language model operations
 * - LLMManager for high-level LLM operations
 * - Vision components for image processing
 * - Voice components for speech recognition and synthesis
 */
@Module
@InstallIn(SingletonComponent::class)
object AIModule {

    @Provides
    @Singleton
    fun provideInferenceEngine(@ApplicationContext context: Context): InferenceEngine {
        return InferenceEngine(context)
    }

    @Provides
    @Singleton
    fun providePhi35MiniLLM(@ApplicationContext context: Context): Phi35MiniLLM {
        return Phi35MiniLLM(context)
    }

    @Provides
    @Singleton
    fun provideLLMManager(@ApplicationContext context: Context): LLMManager {
        return LLMManager(context)
    }

    @Provides
    @Singleton
    fun provideBarcodeScanner(): BarcodeScanner {
        return BarcodeScanner()
    }

    @Provides
    @Singleton
   fun provideTextExtractor(): TextExtractor {
        return TextExtractor()
    }

    @Provides
    @Singleton
    fun provideONTLightDetector(): ONTLightDetector {
        return ONTLightDetector()
    }

    @Provides
    @Singleton
    fun providePhotoQualityAnalyzer(): PhotoQualityAnalyzer {
        return PhotoQualityAnalyzer()
    }

    @Provides
    @Singleton
    fun provideVoiceCommandProcessor(): VoiceCommandProcessor {
        return VoiceCommandProcessor()
    }

    @Provides
    @Singleton
    fun provideTextToSpeech(@ApplicationContext context: Context): TextToSpeech {
        return TextToSpeech(context)
    }

    @Provides
    @Singleton
    fun provideSpeechRecognizer(@ApplicationContext context: Context): SpeechRecognizer {
        return SpeechRecognizer(context)
    }
}