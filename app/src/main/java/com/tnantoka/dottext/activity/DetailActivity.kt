package com.tnantoka.dottext.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tnantoka.dottext.Constants
import com.tnantoka.dottext.R
import java.io.File

class DetailActivity : AppCompatActivity(R.layout.activity_detail) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent?.extras?.let {
            val file = it.getSerializable(Constants.FILE) as File
            supportActionBar?.setTitle(file.name)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
