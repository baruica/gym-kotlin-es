package gym.membership.use_cases

import gym.membership.domain.MemberId
import gym.membership.domain.NewMemberRegistered
import gym.membership.domain.WelcomeEmailWasSentToNewMember
import gym.membership.infrastructure.InMemoryMailer
import gym.membership.infrastructure.MemberInMemoryEventStore
import gym.subscriptions.domain.SubscriptionId
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SendWelcomeEmailToNewMemberTest {

    @Test
    fun handle() {

        val memberId = MemberId("member abc")
        val emailAddress = "bob@gmail.com"
        val subscriptionId = SubscriptionId("subscription def")
        val memberSince = LocalDate.now()

        val memberEventStore = MemberInMemoryEventStore()
        memberEventStore.store(listOf(
            NewMemberRegistered(
                memberId.toString(),
                emailAddress,
                subscriptionId.toString(),
                memberSince.toString()
            )
        ))

        val mailer = InMemoryMailer()

        val tested = SendWelcomeEmailToNewMember(memberEventStore, mailer)

        val events = tested.handle(
            SendWelcomeEmailToNewMemberCommand(memberId.toString())
        )

        assertEquals(
            events.last(),
            WelcomeEmailWasSentToNewMember(
                memberId.toString(),
                emailAddress,
                memberSince.toString()
            )
        )
        assertTrue(mailer.welcomeEmailWasSentTo("bob@gmail.com"))
    }
}
