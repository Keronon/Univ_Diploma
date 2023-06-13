package ru.donntu.admission

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FragmentPageDocs: Fragment()
{
    companion object { val docs = mutableMapOf<String, Any>() }

    @Suppress("PropertyName")
    private lateinit var _this : View

    private lateinit var listAdapter : SimpleAdapter
    private val keys  = arrayOf("file", "path")
    private val items = ArrayList<Map<String, Any>>()
    @Suppress("PrivatePropertyName")
    private val REQ_KEY = 69
    
    private lateinit var popFiles : Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _this = inflater.inflate(R.layout.fragment_page_docs, container, false)

        // hooks

        popFiles = Dialog(_this.context)

        // list

        val list: ListView = _this.findViewById(R.id.VIEW_list)
        listAdapter = SimpleAdapter(
            _this.context, items, android.R.layout.simple_list_item_activated_2,
            keys, intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        list.adapter = listAdapter

        // buttons

        _this.findViewById<Button>(R.id.BTN_files).setOnClickListener {
            show(_this.context, "Отображаем список файлов")
            showPopupFiles()
        }
        _this.findViewById<Button>(R.id.BTN_del).setOnClickListener {
            show(_this.context, "Удаляем")

            if (list.checkedItemCount == 0)
            {
                show(_this.context, "Выберите файл")
                return@setOnClickListener
            }
            items.removeAt(list.checkedItemPosition)
            list.setItemChecked(list.checkedItemPosition, false)
            listAdapter.notifyDataSetChanged()
        }
        _this.findViewById<Button>(R.id.BTN_add).setOnClickListener {
            show(_this.context, "Добавляем")

            val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("*/*")
                .putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf"))
            @Suppress("DEPRECATION")
            startActivityForResult(fileIntent, REQ_KEY)
        }

        return _this
    }

    private fun showPopupFiles()
    {
        popFiles.setContentView(R.layout.popup_files)
        popFiles.findViewById<TextView>(R.id.BTN_close).setOnClickListener{ popFiles.dismiss() }
        popFiles.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_KEY -> if (resultCode == RESULT_OK)
            {
                val fileUri: Uri = data?.data!!
                if (fileUri.toString().startsWith("content://"))
                {
                    var cursor: Cursor? = null
                    try
                    {
                        cursor = _this.context.contentResolver.query(fileUri, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst())
                        {
                            @SuppressLint("Range")
                            val fileName: String = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                            val item: MutableMap<String, Any> = HashMap()
                            item[keys[0]] = fileName
                            item[keys[1]] = fileUri
                            items.add(item)
                            listAdapter.notifyDataSetChanged()
                        }
                    }
                    finally { cursor?.close() }
                }
            }
        }
    }

    override fun onResume()
    {
        FragmentPageConfirm.personalData = null
        super.onResume()
    }

    override fun onPause()
    {
        super.onPause()
        items.forEach { v -> docs[v[keys[0]] as String] = v[keys[1]] as Any }
    }
}