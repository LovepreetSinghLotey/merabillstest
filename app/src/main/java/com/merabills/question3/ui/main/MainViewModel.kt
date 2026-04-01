package com.merabills.question3.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.merabills.question3.models.Row
import com.merabills.question3.models.Square
import java.util.Random

class MainViewModel : ViewModel() {

    private val _rows = MutableLiveData<List<Row>>()
    val rows: LiveData<List<Row>> = _rows

    private val random = Random()

    fun initData(restoredRows: List<Row>?) {
        if (_rows.value == null) {
            if (restoredRows != null) {
                _rows.value = restoredRows
            } else {
                generateData()
            }
        }
    }

    fun generateData() {
        val newRows = ArrayList<Row>(250)
        var squareIndex = 0
        var rowIdCounter = 0
        val totalSquares = 10000

        while (squareIndex < totalSquares) {
            var count = random.nextInt(100) + 1
            if (squareIndex + count > totalSquares) {
                count = totalSquares - squareIndex
            }

            val squaresInRow = ArrayList<Square>(count)
            for (i in 0 until count) {
                val color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
                squaresInRow.add(Square(squareIndex, color))
                squareIndex++
            }
            newRows.add(Row(rowIdCounter++, squaresInRow))
        }
        _rows.value = newRows
    }

    fun removeSquare(square: Square) {
        val currentRows = _rows.value?.toMutableList() ?: return
        val rowIndex = currentRows.indexOfFirst { row -> row.squares.any { it.index == square.index } }
        if (rowIndex != -1) {
            val row = currentRows[rowIndex]
            val newSquares = row.squares.filter { it.index != square.index }
            if (newSquares.isEmpty()) {
                currentRows.removeAt(rowIndex)
            } else {
                currentRows[rowIndex] = row.copy(squares = newSquares)
            }
            _rows.value = currentRows
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        val currentRows = rows.value ?: return
        val totalSquaresCount = currentRows.sumOf { it.squares.size }
        val indices = IntArray(totalSquaresCount)
        val colors = IntArray(totalSquaresCount)
        var k = 0
        val rowCounts = IntArray(currentRows.size)
        val rowIds = IntArray(currentRows.size)

        for (i in currentRows.indices) {
            val row = currentRows[i]
            rowCounts[i] = row.squares.size
            rowIds[i] = row.id
            for (sq in row.squares) {
                indices[k] = sq.index
                colors[k] = sq.color
                k++
            }
        }

        outState.putIntArray("indices", indices)
        outState.putIntArray("colors", colors)
        outState.putIntArray("rowCounts", rowCounts)
        outState.putIntArray("rowIds", rowIds)
    }

    fun restoreData(savedInstanceState: Bundle): List<Row>? {
        val indices = savedInstanceState.getIntArray("indices") ?: return null
        val colors = savedInstanceState.getIntArray("colors") ?: return null
        val rowCounts = savedInstanceState.getIntArray("rowCounts") ?: return null
        val rowIds = savedInstanceState.getIntArray("rowIds") ?: return null

        val restoredRows = ArrayList<Row>(rowCounts.size)
        var currentSquareIndex = 0
        for (i in rowCounts.indices) {
            val count = rowCounts[i]
            val squares = ArrayList<Square>(count)
            for (j in 0 until count) {
                if (currentSquareIndex < indices.size) {
                    squares.add(Square(indices[currentSquareIndex], colors[currentSquareIndex]))
                    currentSquareIndex++
                }
            }
            restoredRows.add(Row(rowIds[i], squares))
        }
        return restoredRows
    }
}