package co.edu.uniquindio.servify.domain.usecase.comment

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verifica la fórmula que usa la transacción de Firestore para actualizar
 * `reviewsCount` y `ratingAverage` de la publicación.
 */
class AddCommentTransactionTest {

    @Test
    fun recalculaPromedioConUnDecimalYCuentaResenas() {
        val totals = ReviewRatingCalculator.calculate(
            currentAverage = 4.0,
            currentCount = 2,
            newRating = 5
        )

        assertEquals(3, totals.reviewsCount)
        assertEquals(4.3, totals.ratingAverage, 0.0001)
    }

    @Test
    fun primeraResenaPromedioEsLaCalificacion() {
        val totals = ReviewRatingCalculator.calculate(
            currentAverage = 0.0,
            currentCount = 0,
            newRating = 5
        )

        assertEquals(1, totals.reviewsCount)
        assertEquals(5.0, totals.ratingAverage, 0.0001)
    }

    @Test
    fun promedioSeRedondeaAMaximoUnDecimal() {
        val totals = ReviewRatingCalculator.calculate(
            currentAverage = 4.0,
            currentCount = 3,
            newRating = 5
        )

        assertEquals(4, totals.reviewsCount)
        assertEquals(4.3, totals.ratingAverage, 0.0001)
    }

    @Test
    fun contadorNegativoSeTrataComoCero() {
        val totals = ReviewRatingCalculator.calculate(
            currentAverage = 0.0,
            currentCount = -5,
            newRating = 4
        )

        assertEquals(1, totals.reviewsCount)
        assertEquals(4.0, totals.ratingAverage, 0.0001)
    }
}
