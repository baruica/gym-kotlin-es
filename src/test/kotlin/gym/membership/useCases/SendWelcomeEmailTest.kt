package gym.membership.useCases

import gym.membership.domain.MemberId
import gym.membership.domain.NewMemberRegistered
import gym.membership.domain.WelcomeEmailWasSentToNewMember
import gym.subscriptions.domain.SubscriptionId
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldEndWith
import java.time.LocalDate

class SendWelcomeEmailTest : AnnotationSpec() {

    @Test
    fun handle() {

        val memberId = MemberId("member abc")
        val emailAddress = "bob@gmail.com"
        val subscriptionId = SubscriptionId("subscription def")
        val memberSince = LocalDate.now()

        val eventStore = InMemoryMemberEventStore()
        eventStore.store(
            listOf(
                NewMemberRegistered(
                    memberId.toString(),
                    emailAddress,
                    subscriptionId.toString(),
                    memberSince.toString()
                )
            )
        )

        val mailer = InMemoryMailer()

        val tested = SendWelcomeEmail(eventStore, mailer)

        val events = tested.handle(
            SendWelcomeEmailCommand(memberId.toString())
        )

        events.shouldEndWith(
            WelcomeEmailWasSentToNewMember(
                memberId.toString(),
                emailAddress,
                memberSince.toString()
            )
        )
        mailer.welcomeEmailWasSentTo("bob@gmail.com").shouldBeTrue()
    }
}
