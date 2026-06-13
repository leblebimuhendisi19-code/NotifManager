package com.kiraldev.notifmanager

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(
    private val allApps: MutableList<AppItem>,
    private val prefs: SharedPreferences
) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    private var filteredApps: MutableList<AppItem> = allApps.toMutableList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.appIcon)
        val name: TextView = view.findViewById(R.id.appName)
        val pkg: TextView = view.findViewById(R.id.appPackage)
        val btnSilent: TextView = view.findViewById(R.id.btnSilent)
        val btnDefault: TextView = view.findViewById(R.id.btnDefault)
        val btnSound: TextView = view.findViewById(R.id.btnSound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = filteredApps[position]
        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.name
        holder.pkg.text = app.packageName

        fun updateButtons() {
            val ctx = holder.itemView.context
            val activeColor = ContextCompat.getColor(ctx, R.color.accent)
            val inactiveColor = ContextCompat.getColor(ctx, R.color.btn_inactive)
            val activeText = ContextCompat.getColor(ctx, android.R.color.white)
            val inactiveText = ContextCompat.getColor(ctx, R.color.text_secondary)

            listOf(holder.btnSilent, holder.btnDefault, holder.btnSound).forEach {
                it.setBackgroundColor(inactiveColor)
                it.setTextColor(inactiveText)
            }

            when (app.mode) {
                "silent" -> { holder.btnSilent.setBackgroundColor(activeColor); holder.btnSilent.setTextColor(activeText) }
                "sound"  -> { holder.btnSound.setBackgroundColor(activeColor); holder.btnSound.setTextColor(activeText) }
                else     -> { holder.btnDefault.setBackgroundColor(activeColor); holder.btnDefault.setTextColor(activeText) }
            }
        }

        updateButtons()

        fun setMode(mode: String) {
            app.mode = mode
            prefs.edit().putString(app.packageName, mode).apply()
            updateButtons()
        }

        holder.btnSilent.setOnClickListener { setMode("silent") }
        holder.btnDefault.setOnClickListener { setMode("default") }
        holder.btnSound.setOnClickListener { setMode("sound") }
    }

    override fun getItemCount() = filteredApps.size

    fun filter(query: String) {
        filteredApps = if (query.isEmpty()) {
            allApps.toMutableList()
        } else {
            allApps.filter { it.name.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }
}
