package net.dankito.text.extraction.app.android.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import net.dankito.text.extraction.app.android.fragment.OpenPdfExtractTextTabFragment
import net.dankito.text.extraction.app.android.fragment.itextExtractTextTabFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MainActivityTabsAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 2
    }


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return OpenPdfExtractTextTabFragment()
            1 -> return itextExtractTextTabFragment()
        }

        // should never come to here
        return null
    }

}