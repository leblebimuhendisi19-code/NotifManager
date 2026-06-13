package com.kiraldev.notifmanager

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchBar: EditText
    private lateinit var btnPermission: Button
    private lateinit var adapter: AppAdapter
    private val prefs by lazy { getSharedPreferences("notif_prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        searchBar = findViewById(R.id.searchBar)
        btnPermission = findViewById(R.id.btnPermission)

        recyclerView.layoutManager = LinearLayoutManager(this)

        btnPermission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        loadApps()

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { adapter.filter(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onResume() {
        super.onResume()
        updatePermissionButton()
    }

    private fun updatePermissionButton() {
        val hasPermission = isNotificationListenerEnabled()
        if (hasPermission) {
            btnPermission.text = "✓ İzin Verildi"
            btnPermission.isEnabled = false
            btnPermission.alpha = 0.5f
        } else {
            btnPermission.text = "⚠ Bildirim İzni Ver"
            btnPermission.isEnabled = true
            btnPermission.alpha = 1f
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val cn = ComponentName(this, NotificationService::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat?.contains(cn.flattenToString()) == true
    }

    private fun loadApps() {
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 || it.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0 }
            .map { AppItem(
                name = pm.getApplicationLabel(it).toString(),
                packageName = it.packageName,
                icon = pm.getApplicationIcon(it.packageName),
                mode = prefs.getString(it.packageName, "default") ?: "default"
            )}
            .sortedBy { it.name.lowercase() }

        adapter = AppAdapter(apps.toMutableList(), prefs)
        recyclerView.adapter = adapter
    }
}
