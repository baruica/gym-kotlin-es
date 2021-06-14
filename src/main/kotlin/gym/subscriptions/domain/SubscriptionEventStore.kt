package gym.subscriptions.domain

import common.EventStore
import java.time.LocalDate

interface SubscriptionEventStore : EventStore<Subscription> {

    fun get(subscriptionId: String): Subscription

    fun endedMonthlySubscriptions(date: LocalDate): List<Subscription>

    fun onGoingSubscriptions(date: LocalDate): List<Subscription>

    fun threeYearsAnniversarySubscriptions(date: LocalDate): List<Subscription>
}
