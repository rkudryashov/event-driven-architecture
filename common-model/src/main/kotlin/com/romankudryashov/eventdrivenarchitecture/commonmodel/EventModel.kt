package com.romankudryashov.eventdrivenarchitecture.commonmodel

enum class AggregateType {
    Book,
    Author,
    Notification,
}

enum class EventType {
    // events
    BookCreated,
    BookChanged,
    BookDeleted,
    BookLent,
    BookLoanCanceled,
    BookReturned,
    AuthorChanged,

    // commands
    RollbackBookLentCommand,
    SendNotificationCommand
}
