package com.merabills.question3.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Square(val index: Int, val color: Int) : Parcelable

@Parcelize
data class Row(val id: Int, val squares: List<Square>) : Parcelable
