package ru.semyon.model

sealed class GenerationResult {
    data class Success(val result: String) : GenerationResult()
    data class Failure(val error: String) : GenerationResult()
}