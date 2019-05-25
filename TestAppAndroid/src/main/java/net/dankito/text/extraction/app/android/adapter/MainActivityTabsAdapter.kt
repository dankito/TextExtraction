package net.dankito.text.extraction.app.android.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import net.dankito.text.extraction.app.android.fragment.OpenPdfExtractTextTabFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MainActivityTabsAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 1
    }


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return OpenPdfExtractTextTabFragment()
        }

        // should never come to here
        return null
    }

}