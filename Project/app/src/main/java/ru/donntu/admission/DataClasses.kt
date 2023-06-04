package ru.donntu.admission

data class Own (
    var lang        : String     = "",
    var country     : String     = "",
    var inn         : String     = "",
    var dormitory   : Boolean    = false,
    val reg : OwnAddress = OwnAddress(),
    val live: OwnAddress = OwnAddress()
){fun clear() {
    lang         = ""
    country      = ""
    inn          = ""
    dormitory    = false
    reg.clear()
    live.clear()
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
    var prevEducations: String = "",
    val baseDocs      : MutableList<BaseDoc> = mutableListOf(BaseDoc())
){fun clear() {
    prevEducations = ""
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
    var own              : Own                     = Own(),
    var parents          : Parents                 = Parents(),
    val docs             : MutableMap<String, Any> = mutableMapOf(),
    var baseDocsInfo     : BaseDocsInfo            = BaseDocsInfo(),
    val cases            : MutableList<Case>       = mutableListOf(Case()),
    var priority_contract: String  = ""
){fun clear() {
    own         .clear()
    parents     .clear()
    docs        .clear()
    baseDocsInfo.clear()
    cases       .clear()
    priority_contract = ""
}}