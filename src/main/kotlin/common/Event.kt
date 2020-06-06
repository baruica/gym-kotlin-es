package common

import java.time.Instant

interface Event {

    val created: Instant
}
