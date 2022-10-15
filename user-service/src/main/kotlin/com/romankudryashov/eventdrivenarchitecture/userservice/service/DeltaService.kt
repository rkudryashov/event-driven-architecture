package com.romankudryashov.eventdrivenarchitecture.userservice.service

interface DeltaService {
    fun <T> getDelta(currentObjectState: T?, previousObjectState: T?): Map<String, Pair<Any?, Any?>>
}
