package common

sealed class Either<out L, out R> {
    data class Left<out L, out R>(val a: L) : Either<L, R>()
    data class Right<out L, out R>(val b: R) : Either<L, R>()
}

fun <E> E.left() = Either.Left<E, Nothing>(this)

fun <T> T.right() = Either.Right<Nothing, T>(this)

fun <L, R, B> Either<L, R>.map(f: (R) -> B): Either<L, B> = when (this) {
    is Either.Left -> this.a.left()
    is Either.Right -> f(this.b).right()
}

fun <L, R, B> Either<L, R>.flatMap(f: (R) -> Either<L, B>): Either<L, B> = when (this) {
    is Either.Left -> this.a.left()
    is Either.Right -> f(this.b)
}
