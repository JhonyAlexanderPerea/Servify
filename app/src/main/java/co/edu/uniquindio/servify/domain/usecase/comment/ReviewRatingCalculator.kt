package co.edu.uniquindio.servify.domain.usecase.comment

/**
 * Cálculo puro del nuevo promedio y contador de reseñas de una publicación.
 *
 * Se mantiene fuera de la transacción de Firestore para que la fórmula pueda
 * verificarse con pruebas unitarias sin depender de Android ni de Firebase.
 */
object ReviewRatingCalculator {

    /**
     * Recalcula el promedio redondeado a 1 decimal tras añadir [newRating].
     * Ejemplo: promedio 4.0 con 2 reseñas + nueva reseña de 5.0 => 4.3 con 3 reseñas.
     */
    fun calculate(
        currentAverage: Double,
        currentCount: Int,
        newRating: Int
    ): ReviewTotals {
        val safeCount = currentCount.coerceAtLeast(0)
        val nextCount = safeCount + 1
        val nextAverage = ((currentAverage * safeCount) + newRating) / nextCount
        return ReviewTotals(
            reviewsCount = nextCount,
            ratingAverage = Math.round(nextAverage * 10.0) / 10.0
        )
    }

    data class ReviewTotals(
        val reviewsCount: Int,
        val ratingAverage: Double
    )
}
