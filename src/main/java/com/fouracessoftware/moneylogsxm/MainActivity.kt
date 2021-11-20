package com.fouracessoftware.moneylogsxm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.iterator

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fouracessoftware.moneylogsxm.datadeal.Central


class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Central.activate(applicationContext)

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment_container_view) as NavHostFragment
    navController = navHostFragment.navController
    appBarConfiguration = AppBarConfiguration(navController.graph)
        val barra = findViewById<Toolbar>(R.id.toolbar)
        for(i in barra.menu){
            i.isVisible = false;
        }
        barra.menu.findItem(R.id.chronoview).isVisible = true
        barra.setupWithNavController(navController,appBarConfiguration)
        //(requireActivity() as MainActivity).setSupportActionBar(barra)
//    setupActionBarWithNavController(navController, appBarConfiguration)
}

override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_fragment_container_view)
    return navController.navigateUp(appBarConfiguration)
            || super.onSupportNavigateUp()
}
}