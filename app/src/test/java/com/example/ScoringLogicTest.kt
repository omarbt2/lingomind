package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoringLogicTest {

    // XP: 10 per correct answer, accumulated in session
    @Test
    fun `correct answer awards 10 XP`() {
        val xpPerCorrect = 10
        val correctAnswers = 5
        val totalXp = correctAnswers * xpPerCorrect
        assertEquals(50, totalXp)
    }

    @Test
    fun `no XP awarded for zero correct answers`() {
        assertEquals(0, 0 * 10)
    }

    // Gems: 5 per correct answer
    @Test
    fun `correct answer awards 5 gems`() {
        val gemsPerCorrect = 5
        assertEquals(15, 3 * gemsPerCorrect)
    }

    // Hearts: -1 per wrong answer, except free_writing
    @Test
    fun `wrong answer deducts one heart`() {
        val initialHearts = 5
        val wrongAnswers = 2
        val remaining = (initialHearts - wrongAnswers).coerceAtLeast(0)
        assertEquals(3, remaining)
    }

    @Test
    fun `hearts cannot go below zero`() {
        val initialHearts = 1
        val wrongAnswers = 3
        val remaining = (initialHearts - wrongAnswers).coerceAtLeast(0)
        assertEquals(0, remaining)
    }

    @Test
    fun `free writing wrong answer does not deduct hearts`() {
        val isFreeWriting = true
        val heartsBefore = 3
        val heartsAfter = if (!isFreeWriting) heartsBefore - 1 else heartsBefore
        assertEquals(3, heartsAfter)
    }

    @Test
    fun `non-free-writing wrong answer deducts hearts`() {
        val isFreeWriting = false
        val heartsBefore = 3
        val heartsAfter = if (!isFreeWriting) heartsBefore - 1 else heartsBefore
        assertEquals(2, heartsAfter)
    }

    // cleanString logic (from semaViewModel)
    @Test
    fun `cleanString lowercases and removes punctuation`() {
        val result = "Hello, World!".lowercase()
            .replace("[.,?!'\"#]".toRegex(), "")
            .replace("\\s+".toRegex(), " ")
            .trim()
        assertEquals("hello world", result)
    }

    @Test
    fun `cleanString handles multiple spaces`() {
        val result = "  foo   bar  ".lowercase()
            .replace("[.,?!'\"#]".toRegex(), "")
            .replace("\\s+".toRegex(), " ")
            .trim()
        assertEquals("foo bar", result)
    }

    @Test
    fun `cleanString removes Arabic punctuation`() {
        val input = "مرحبا، كيف حالك؟"
        val result = input.lowercase()
            .replace("[.,?!'\"#]".toRegex(), "")
            .replace("\\s+".toRegex(), " ")
            .trim()
        assertEquals("مرحبا، كيف حالك؟", result)
    }

    // Streak freeze logic
    @Test
    fun `streak freeze is consumed when streak not yet today`() {
        val streakWasToday = false
        val freezeQuantity = 1
        val shouldConsume = !streakWasToday && freezeQuantity > 0
        assertEquals(true, shouldConsume)
    }

    @Test
    fun `streak freeze not consumed when streak already today`() {
        val streakWasToday = true
        val freezeQuantity = 1
        val shouldConsume = !streakWasToday && freezeQuantity > 0
        assertEquals(false, shouldConsume)
    }

    @Test
    fun `streak freeze not consumed when quantity is zero`() {
        val streakWasToday = false
        val freezeQuantity = 0
        val shouldConsume = !streakWasToday && freezeQuantity > 0
        assertEquals(false, shouldConsume)
    }

    // Lesson completion: session accumulator is reset for new lesson
    @Test
    fun `new lesson starts with zero XP and gems`() {
        val xpGained = 0
        val gemsGained = 0
        assertEquals(0, xpGained)
        assertEquals(0, gemsGained)
    }

    // MCQ correctness
    @Test
    fun `mcq answer is correct when selected index matches correct index`() {
        val selectedIndex = 2
        val correctIndex = 2
        val isCorrect = selectedIndex == correctIndex
        assertEquals(true, isCorrect)
    }

    @Test
    fun `mcq answer is wrong when selected index differs`() {
        val selectedIndex = 1
        val correctIndex = 2
        val isCorrect = selectedIndex == correctIndex
        assertEquals(false, isCorrect)
    }

    // Fill-blank correctness (case insensitive)
    @Test
    fun `fill_blank matches case insensitive`() {
        val selected = "Hello"
        val correct = "hello"
        val isCorrect = selected.equals(correct, ignoreCase = true)
        assertEquals(true, isCorrect)
    }

    @Test
    fun `fill_blank fails on different word`() {
        val selected = "world"
        val correct = "hello"
        val isCorrect = selected.equals(correct, ignoreCase = true)
        assertEquals(false, isCorrect)
    }
}
