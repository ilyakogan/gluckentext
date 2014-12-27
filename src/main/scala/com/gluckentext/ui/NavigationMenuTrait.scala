package com.gluckentext.ui

import android.app.Activity
import android.content.Intent
import android.view.{MenuItem, Menu}
import com.gluckentext.R
import org.scaloid.common.SActivity

trait NavigationMenuTrait extends SActivity {
  this : Activity =>

  override def onCreateOptionsMenu(menu: Menu) = {
    val inflater = getMenuInflater
    inflater.inflate(R.menu.navigaiton_menu, menu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem) = {
    item.getItemId match {
      case R.id.action_new_quiz => startActivity(new Intent(this, classOf[PracticeSelectionActivity])); true
      case R.id.action_change_language => startActivity(new Intent(this, classOf[LanguageSelectionActivity])); true
      case _ => super.onOptionsItemSelected(item)
    }
  }

}
