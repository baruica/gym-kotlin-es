package gym.membership.useCases

import gym.membership.domain.NewMemberRegistered
import gym.membership.domain.WelcomeEmailWasSentToNewMember
import gym.membership.infrastructure.InMemoryMailer
import gym.membership.infrastructure.InMemoryMemberEventStore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldEndWith
import java.time.LocalDate

class SendWelcomeEmailTest : AnnotationSpec() {

    @Test
    fun handle() {

        val memberId = "member abc"
        val emailAddress = "bob@gmail.com"
        val subscriptionId = "subscription def"
        val memberSince = LocalDate.now()

        val eventStore = InMemoryMemberEventStore()
        eventStore.storeEvents(
            listOf(
                NewMemberRegistered(
                    memberId,
                    emailAddress,
                    subscriptionId,
                    memberSince.toString()
                )
            )
        )

        val mailer = InMemoryMailer()

        val tested = SendWelcomeEmail(eventStore, mailer)

        val events = tested(
            SendWelcomeEmailCommand(memberId)
        )

        events.shouldEndWith(
            WelcomeEmailWasSentToNewMember(
                memberId,
                emailAddress,
                memberSince.toString()
            )
        )
        mailer.welcomeEmailWasSentTo("bob@gmail.com").shouldBeTrue()
    }
}
