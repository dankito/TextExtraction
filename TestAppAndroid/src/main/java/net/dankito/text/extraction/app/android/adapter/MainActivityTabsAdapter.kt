package net.dankito.text.extraction.app.android.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import net.dankito.text.extraction.app.android.fragment.ExtractTextTabFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MainActivityTabsAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        // Show 3 total pages.
        return 3
    }


    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a ExtractTextTabFragment (defined as a static inner class below).
        return ExtractTextTabFragment.newInstance(position + 1)
    }

}