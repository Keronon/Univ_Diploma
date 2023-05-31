package ru.donntu.admission

data class Own (
    var lang        : String = "",
    var country     : String = "",
    var inn         : String = "",
    var dormitory   : Boolean = false,
    val address_reg : OwnAddress = OwnAddress(),
    val address_live: OwnAddress = OwnAddress()
){fun clear() {
    lang         = ""
    country      = ""
    inn          = ""
    dormitory    = false
    address_reg.clear()
    address_live.clear()
}}
data class OwnAddress (
    var region  : String = "",
    var city    : String = "",
    var district: String = "",
    var street  : String = "",
    var house   : String = "",
    var index   : String = ""
){fun clear() {
    region   = ""
    city     = ""
    district = ""
    street   = ""
    house    = ""
    index    = ""
}}

data class Parents (
    var father : Human = Human(),
    var mother : Human = Human()
){fun clear() {
    father.clear()
    mother.clear()
}}
data class Human (
    var surname   : String = "",
    var name      : String = "",
    var fathername: String = "",
    var phone     : String = "",
    var reg       : String = ""
){fun clear() {
    surname    = ""
    name       = ""
    fathername = ""
    phone      = ""
    reg        = ""
}}

data class BaseDocsInfo (
    val prevEducations: MutableList<String>  = mutableListOf(),
    val baseDocs      : MutableList<BaseDoc> = mutableListOf()
){fun clear() {
    prevEducations.clear()
    baseDocs      .clear()
}}
data class BaseDoc (
    var op         : String = "",
    var educ       : String = "",
    var educ_status: String = ""
){fun clear() {
    op          = ""
    educ        = ""
    educ_status = ""
}}

data class Case (
    var priority: String = "",
    var fo      : String = "",
    var course  : String = "",
    var stream  : String = ""
){fun clear() {
    priority = ""
    fo       = ""
    course   = ""
    stream   = ""
}}

data class PersonalData (
    var own         : Own          = Own         (),
    var parents     : Parents      = Parents     (),
    var docs        : String       = String      (),
    var baseDocsInfo: BaseDocsInfo = BaseDocsInfo(),
    var cases       : MutableList<Case> = mutableListOf()
){fun clear() {
    own         .clear()
    parents     .clear()
    docs = ""
    baseDocsInfo.clear()
    cases       .clear()
}}