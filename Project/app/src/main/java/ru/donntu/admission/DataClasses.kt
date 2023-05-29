package ru.donntu.admission

data class Own (
    val lang        : String,
    val country     : String,
    val inn         : String,
    val dormitory   : Boolean,
    val address_reg : OwnAddress,
    val address_live: OwnAddress
)

data class OwnAddress (
    val region  : String,
    val city    : String,
    val district: String,
    val street  : String,
    val house   : String,
    val index   : String
)

data class Human (
    val surname   : String,
    val name      : String,
    val fathername: String,
    val phone     : String,
    val reg       : String
)

data class Case (
    val priority: String,
    val fo      : String,
    val course  : String,
    val stream  : String
)