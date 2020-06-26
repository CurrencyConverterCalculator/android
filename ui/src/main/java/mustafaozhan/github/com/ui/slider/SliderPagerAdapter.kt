/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package mustafaozhan.github.com.ui.slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.github.mustafaozhan.scopemob.castTo
import com.github.mustafaozhan.ui.R

class SliderPagerAdapter(var context: Context) : PagerAdapter() {

    private var layouts = intArrayOf(
        R.layout.slide_intro,
        R.layout.slide_bug_report,
        R.layout.slide_disable_ads
    )

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val view = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            ?.castTo<LayoutInflater>()
            ?.inflate(layouts[position], container, false)
        container.addView(view)
        return view ?: View(context)
    }

    override fun getCount() = layouts.size

    override fun isViewFromObject(view: View, obj: Any) = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) =
        container.removeView(obj.castTo<View>())
}