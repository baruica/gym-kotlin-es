package gym.membership.use_cases

import gym.membership.domain.EmailAddress
import gym.membership.domain.NewMembership
import gym.membership.infrastructure.MemberInMemoryRepository
import gym.subscriptions.domain.NewSubscription
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NewSubscriptionEventListenerTest {

    @Test
    fun handle() {
        val memberRepository = MemberInMemoryRepository()

        val email = "luke@gmail.com"

        assertNull(memberRepository.findByEmail(EmailAddress(email)))

        val subscriptionId = "subscriptionId def"
        val subscriptionStartDate = "2018-06-05"
        val newSubscriptionEvent = NewSubscription(
            subscriptionId,
            400,
            12,
            subscriptionStartDate,
            "2019-06-06",
            email,
            false
        )

        val tested = NewSubscriptionEventListener(memberRepository)
        val events = tested.handle(
            newSubscriptionEvent
        )

        assertEquals(
            events.last(),
            NewMembership(
                events.last().memberId,
                email,
                subscriptionId,
                subscriptionStartDate
            )
        )
    }
}
