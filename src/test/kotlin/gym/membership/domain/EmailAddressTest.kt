package gym.membership.domain

import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class EmailAddressTest {

    @Test
    fun `does not allow an invalid email`() {
        assertFailsWith<IllegalArgumentException> {
            EmailAddress("bob[at]gmail.com")
        }
    }
}
