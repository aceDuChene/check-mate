package com.oukschub.checkmate.ui.profile

import androidx.lifecycle.ViewModel
import com.oukschub.checkmate.util.FirebaseUtil

class ProfileViewModel : ViewModel() {
    fun getDisplayName(): String {
        return FirebaseUtil.getDisplayName()
    }

    fun signOut() {
        FirebaseUtil.signOut()
    }
}