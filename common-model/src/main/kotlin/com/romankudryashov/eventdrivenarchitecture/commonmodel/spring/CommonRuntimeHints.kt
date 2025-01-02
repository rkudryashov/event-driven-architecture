package com.romankudryashov.eventdrivenarchitecture.commonmodel.spring

import com.romankudryashov.eventdrivenarchitecture.commonmodel.Author
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Book
import com.romankudryashov.eventdrivenarchitecture.commonmodel.BookLoan
import com.romankudryashov.eventdrivenarchitecture.commonmodel.CurrentAndPreviousState
import com.romankudryashov.eventdrivenarchitecture.commonmodel.Notification
import com.romankudryashov.eventdrivenarchitecture.commonmodel.OutboxMessage
import org.flywaydb.core.internal.publishing.PublishingConfigurationExtension
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import java.util.UUID

class CommonRuntimeHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection()
            // required for JSON serialization/deserialization
            .registerType(Book::class.java, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(Author::class.java, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(BookLoan::class.java, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(CurrentAndPreviousState::class.java, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(Notification::class.java, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            .registerType(OutboxMessage::class.java, MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            // probably, this should be removed sometime in the future
            .registerTypeIfPresent(classLoader, "kotlin.collections.EmptyList", MemberCategory.DECLARED_FIELDS)
            // required to persist entities
            // TODO: remove after https://hibernate.atlassian.net/browse/HHH-16809
            .registerType(Array<UUID>::class.java, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
            // required to use Flyway
            // TODO: remove after https://github.com/oracle/graalvm-reachability-metadata/issues/424
            .registerType(PublishingConfigurationExtension::class.java, MemberCategory.INVOKE_PUBLIC_METHODS)
    }
}
