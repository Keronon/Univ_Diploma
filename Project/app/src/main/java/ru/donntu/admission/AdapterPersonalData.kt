package ru.donntu.admission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterPersonalData(fragment: FragmentActivity) : FragmentStateAdapter(fragment)
{
    val fragments = arrayOfNulls<Fragment>(5)

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(pos: Int): Fragment
    {
        when (pos) {
            0 -> fragments[pos] = FragmentPageOwnData()
            1 -> fragments[pos] = FragmentPageParentsData()
            2 -> fragments[pos] = FragmentPageDocs()
            3 -> fragments[pos] = FragmentPageBaseDocs()
            4 -> fragments[pos] = FragmentPageQuestionary((fragments[3] as FragmentPageBaseDocs).SWITCH_doc_2)
            else -> throw Error("Incorrect fragment")
        }
        return fragments[pos]!!
    }
}