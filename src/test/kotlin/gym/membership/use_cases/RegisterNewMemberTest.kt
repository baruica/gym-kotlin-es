package gym.membership.use_cases

import gym.membership.domain.EmailAddress
import gym.membership.domain.NewMemberRegistered
import gym.membership.infrastructure.MemberInMemoryEventStore
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class RegisterNewMemberTest {

    @Test
    fun handle() {
        val memberEventStore = MemberInMemoryEventStore()

        val email = "luke@gmail.com"

        assertNull(memberEventStore.findByEmailAddress(EmailAddress(email)))

        val subscriptionId = "subscriptionId def"
        val subscriptionStartDate = "2018-06-05"

        val tested = RegisterNewMember(memberEventStore)

        val events = tested.handle(RegisterNewMemberCommand(
            subscriptionId,
            "2018-06-05",
            email
        ))

        assertEquals(
            events.last(),
            NewMemberRegistered(
                events.last().aggregateId(),
                email,
                subscriptionId,
                subscriptionStartDate
            )
        )
    }
}
