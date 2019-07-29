package com.example.juanshichang.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zhy.autolayout.AutoFrameLayout
import com.zhy.autolayout.AutoLinearLayout
import com.zhy.autolayout.AutoRelativeLayout

open class AutoLayoutActivity : AppCompatActivity() {
    companion object {
        private const val LAYOUT_LINEARLAYOUT = "LinearLayout"
        private const val LAYOUT_FRAMELAYOUT = "FrameLayout"
        private const val LAYOUT_RELATIVELAYOUT = "RelativeLayout"
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        var view: View? = null
        if (name == LAYOUT_FRAMELAYOUT) {
            view = AutoFrameLayout(context, attrs)
        }

        if (name == LAYOUT_LINEARLAYOUT) {
            view = AutoLinearLayout(context, attrs)
        }

        if (name == LAYOUT_RELATIVELAYOUT) {
            view = AutoRelativeLayout(context, attrs)
        }
//        if (view != null) return view
        return view ?: super.onCreateView(name, context, attrs)
//        return super.onCreateView(parent, name, context, attrs)
    }
}