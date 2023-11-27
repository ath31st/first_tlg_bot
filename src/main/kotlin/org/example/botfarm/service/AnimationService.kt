package org.example.botfarm.service

/**
 * Service class for generating a visual representation of snow animation.
 *
 * This class provides methods to create a snow animation field, shift the snow
 * to the left or right, and add additional snow layers at specific shifts.
 *
 * @property width The width of the snow animation field.
 * @property height The height of the snow animation field.
 */
class AnimationService {
    private val width = 20
    private val height = 10

    /**
     * Gets the snow animation at a specific shift.
     *
     * @param shift The shift value for the animation.
     * @return The snow animation as a formatted string.
     */
    fun getSnow(shift: Int): String {
        val field = genField()
        if (shift % 2 == 0) {
            shiftSnowRight(field)
        } else {
            shiftSnowLeft(field)
        }
        if (shift >= 5) field[height - 3].fill('*')
        if (shift >= 9) field[height - 4].fill('*')
        return "```\n" + field.joinToString("\n") { it.joinToString("") } + "```"
    }

    private fun shiftSnowLeft(field: Array<CharArray>) {
        for (i in 0 until height) {
            val firstCell = field[i][0]
            for (j in 0 until width - 1) {
                field[i][j] = field[i][j + 1]
            }
            field[i][width - 1] = firstCell
        }
    }

    private fun shiftSnowRight(field: Array<CharArray>) {
        for (i in 0 until height) {
            val lastCell = field[i][width - 1]
            for (j in width - 1 downTo 1) {
                field[i][j] = field[i][j - 1]
            }
            field[i][0] = lastCell
        }
    }


    private fun genField(): Array<CharArray> {
        val field = Array(height) { CharArray(width) { ' ' } }
        for (i in 1 until height step 2) {
            for (j in 1 until width step 3) {
                field[i][j] = '*'
            }
        }
        for (i in 0 until height step 2) {
            for (j in 0 until width step 3) {
                field[i][j] = '*'
            }
        }
        field[height - 1].fill('*')
        field[height - 2].fill('*')

        return field
    }
}
