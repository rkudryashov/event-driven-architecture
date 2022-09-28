package com.romankudryashov.eventdrivenarchitecture.bookservice.service.converter

interface Converter<S, T> {
    fun convert(source: S): T
}
