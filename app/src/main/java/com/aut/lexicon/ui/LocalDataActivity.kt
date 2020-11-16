package com.aut.lexicon.ui

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.viewpager2.widget.ViewPager2
import com.aut.lexicon.R
import com.aut.lexicon.adapter.FragmentAdapter
import com.aut.lexicon.library.loader.LocalAudioLoader
import com.aut.lexicon.viewmodel.LocalDataViewModel
import kotlinx.android.synthetic.main.activity_local_data.*


class LocalDataActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private val localDataViewModel: LocalDataViewModel by viewModels()
    private lateinit var mLoaderManager: LoaderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_data)

        mLoaderManager = LoaderManager.getInstance(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        view_pager.adapter = FragmentAdapter(this,
            SingleDataFragment.newInstance(),
            DictionaryDataFragment.newInstance())
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                localDataViewModel.pageChanged(position)
            }
        })
        segment.setOnCheckedChangeListener { _, checkedId ->
            localDataViewModel.buttonChecked(checkedId)
        }
        localDataViewModel.navigateToRadio.observe(this, {
            it?.getContentIfNotHandled()?.let { position ->
                segment.check(if (position == 1) {
                    R.id.rb2
                } else {
                    R.id.rb1
                })
            }
        })
        localDataViewModel.navigateToPage.observe(this, {
            it?.getContentIfNotHandled()?.let { id ->
                view_pager.currentItem = if (id == R.id.rb2) {
                    1
                } else {
                    0
                }
            }
        })

        getData()
    }

    fun getData() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION)
        } else {
            mLoaderManager.initLoader(LOADER_ID, null, this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "权限悲剧", Toast.LENGTH_SHORT).show()
        } else {
            mLoaderManager.initLoader(LOADER_ID, null, this)
        }
    }


    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return LocalAudioLoader(context = this)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        localDataViewModel.swipeCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        localDataViewModel.swipeCursor(null)
    }

    override fun onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID)
        super.onDestroy()
    }

    companion object {
        private const val LOADER_ID = 0x001
        private const val REQUEST_PERMISSION = 0x002
    }

}