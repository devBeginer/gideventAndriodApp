package ru.gidevent.androidapp.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

fun View.onClickDebounce(debounceDuration: Long = 900L, action: (View) -> Unit) =
    setOnClickListener(DebouncedOnClickListener(debounceDuration, action))


private class DebouncedOnClickListener(
    private val debounceDuration: Long,
    private val clickAction: (View) -> Unit
) : View.OnClickListener {
    private var lastClickTime: Long = 0L
    override fun onClick(v: View) {
        val now = System.currentTimeMillis()
        if ((now - lastClickTime) > debounceDuration) {
            lastClickTime = now
            clickAction(v)
        }
    }
}

fun Fragment?.runOnUiThread(action: () -> Unit) {
    this ?: return
    if (!isAdded) return // Fragment not attached to an Activity
    activity?.runOnUiThread(action)
}
fun Fragment?.showSnack(view: View, text:String, maxLine:Int){
    val snack: Snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG)
    val tv = snack.view.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
    tv.maxLines = maxLine
    snack.show()
}

fun Fragment?.showShortToast(context: Context, text: String){
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun Fragment?.showLongToast(context: Context, text: String){
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun Context.copyToClipboard(text:String){
    val clipboard: ClipboardManager? = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("", text)
    clipboard?.setPrimaryClip(clip)
}

fun ignoreCaseOpt(ignoreCase: Boolean) =
    if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()

fun String?.indexesOf(pat: String, ignoreCase: Boolean = true): List<Int> =
    pat.toRegex(ignoreCaseOpt(ignoreCase))
        .findAll(this ?: "")
        .map { it.range.first }
        .toList()

fun <T> LiveData<T>.observeFutureEvents(owner: LifecycleOwner, observer: Observer<T>) {
    observe(owner, object : Observer<T> {
        var isFirst = true

        override fun onChanged(value: T) {
            if (isFirst) {
                isFirst = false
            } else {
                observer.onChanged(value)
            }
        }
    })
}