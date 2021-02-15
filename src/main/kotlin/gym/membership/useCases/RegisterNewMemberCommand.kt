package gym.membership.useCases

data class RegisterNewMemberCommand(
    val subscriptionId: String,
    val subscriptionStartDate: String,
    val email: String,
)
