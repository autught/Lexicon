package com.aut.lexicon.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aut.lexicon.R
import com.aut.lexicon.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModel.Factory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
            if (it.itemId == R.id.menu) {
                Toast.makeText(this, "历史记录", Toast.LENGTH_LONG).show()
            }
            return@OnMenuItemClickListener true
        })
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, HomeFragment.newInstance())
            .commit()
    }


}