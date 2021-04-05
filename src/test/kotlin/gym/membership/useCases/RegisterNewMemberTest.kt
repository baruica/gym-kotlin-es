package gym.membership.useCases

import gym.membership.domain.EmailAddress
import gym.membership.domain.NewMemberRegistered
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class RegisterNewMemberTest {

    @Test
    fun handle() {
        val eventStore = InMemoryMemberEventStore()

        val email = "luke@gmail.com"

        assertNull(eventStore.findByEmailAddress(EmailAddress(email)))

        val subscriptionId = "subscriptionId def"
        val subscriptionStartDate = "2018-06-05"

        val tested = RegisterNewMember(eventStore)

        val events = tested.handle(
            RegisterNewMemberCommand(
                subscriptionId,
                "2018-06-05",
                email
            )
        )

        assertEquals(
            NewMemberRegistered(
                events.last().getAggregateId(),
                email,
                subscriptionId,
                subscriptionStartDate
            ),
            events.last()
        )
    }
}
