package com.gluckentext.ui

import com.gluckentext.datahandling.Persistence
import org.scaloid.common.SActivity

class ZeroActivity extends SActivity {
  onCreate {
    if (!new Persistence().loadHasLanguageEverBeenSelected)
      startActivity[LanguageSelectionActivity]
    else
      startActivity[PracticeSelectionActivity]
  }
}
