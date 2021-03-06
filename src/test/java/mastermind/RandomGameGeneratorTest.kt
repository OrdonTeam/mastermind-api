package mastermind

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert
import org.junit.Test

class RandomGameGeneratorTest {

    @Test
    fun shouldGenerateRandomGame() {
        val random = mock<Function0<Int>>()
        whenever(random.invoke()).thenReturn(0)
        Assert.assertEquals(Mastermind(ColorSet(0, 0, 0, 0)), RandomGameGenerator(random).invoke())
    }

    @Test
    fun shouldGenerateRandomGameWithFixMaxColorValue() {
        val random = mock<Function0<Int>>()
        whenever(random.invoke()).thenReturn(6).thenReturn(7).thenReturn(8).thenReturn(9)
        Assert.assertEquals(Mastermind(ColorSet(6, 7, 0, 1)), RandomGameGenerator(random).invoke())
    }

    @Test
    fun shouldGenerateRandomGameWithFixMaxColorValueSecondCase() {
        val random = mock<Function0<Int>>()
        whenever(random.invoke()).thenReturn(8).thenReturn(9).thenReturn(3).thenReturn(9)
        Assert.assertEquals(Mastermind(ColorSet(0, 1, 3, 1)), RandomGameGenerator(random).invoke())
    }

    @Test
    fun shouldConvertNegativeNumbersToProperPositiveNumbers() {
        val random = mock<Function0<Int>>()
        whenever(random.invoke()).thenReturn(-1).thenReturn(-9).thenReturn(-7).thenReturn(-8)
        Assert.assertEquals(Mastermind(ColorSet(7, 7, 1, 0)), RandomGameGenerator(random).invoke())
    }
}
