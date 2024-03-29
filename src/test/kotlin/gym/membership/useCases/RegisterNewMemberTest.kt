package gym.membership.useCases

import Id
import gym.membership.domain.EmailAddress
import gym.membership.domain.NewMemberRegistered
import gym.membership.infrastructure.InMemoryMemberEventStore
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldEndWith
import io.kotest.matchers.nulls.shouldBeNull
import java.time.LocalDate

internal class RegisterNewMemberTest : StringSpec({

    "handle" {
        val eventStore = InMemoryMemberEventStore()

        val email = "luke@gmail.com"

        eventStore.findByEmailAddress(EmailAddress(email)).shouldBeNull()

        val subscriptionId = "subscriptionId def"
        val subscriptionStartDate = "2018-06-05"

        val tested = RegisterNewMember.Handler(eventStore)

        val events = tested(
            RegisterNewMember(
                Id(subscriptionId),
                LocalDate.parse(subscriptionStartDate),
                EmailAddress(email)
            )
        )

        events.shouldEndWith(
            NewMemberRegistered(
                events.last().getAggregateId(),
                email,
                subscriptionId,
                subscriptionStartDate
            )
        )
    }
})
