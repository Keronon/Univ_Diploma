package ru.donntu.admission

import java.sql.*

@Suppress("ClassName")
class DB_processor
{
    companion object
    {
        private var connection: Connection? = null
        private var statement : Statement?  = null

        fun connect()
        {
            if (connection == null)
            {
                try
                {
                    connection = DriverManager.getConnection(
                        "jdbc:postgresql://10.0.2.2:5432/DonNTU_Admission",
                        "postgres", "root"
                    )
                    statement = connection!!.createStatement()
                }
                catch (e: SQLException) { throw e }
                catch (e: Exception) { throw e }
            }
        }

        fun querySelect(query: String): MutableList<MutableList<Any>>
        {
            if (statement == null) throw Error("Need DB_processor Connect()")

            try
            {
                val resultSet = statement!!.executeQuery(query)
                val columnCount = resultSet.metaData.columnCount

                val rows = mutableListOf<MutableList<Any>>()
                while (resultSet.next())
                {
                    val values = mutableListOf<Any>()
                    for (i in 1..columnCount) { values.add(resultSet.getObject(i)) }
                    rows.add(values)
                }

                resultSet.close()
                return rows
            }
            catch(e: SQLException) { e.printStackTrace() }

            return mutableListOf()
        }

        fun queryUpdate(query: String): Int
        {
            if (statement == null) throw Error("Need > DB_processor : Connect()")

            try { return statement!!.executeUpdate(query) }
            catch(e: SQLException) { e.printStackTrace() }

            return -1
        }

        fun disconnect()
        {
            statement?.close()
            statement = null
            connection?.close()
            connection = null
        }
    }
}