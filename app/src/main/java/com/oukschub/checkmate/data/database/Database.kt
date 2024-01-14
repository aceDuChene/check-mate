package com.oukschub.checkmate.data.database

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.oukschub.checkmate.data.model.Checklist
import com.oukschub.checkmate.data.model.ChecklistItem
import com.oukschub.checkmate.util.FirebaseUtil

class Database {
    private val firestore = Firebase.firestore

    fun addUser(displayName: String) {
        firestore.collection(USERS_COLLECTION)
            .document(FirebaseUtil.getUserId())
            .set(mapOf(USER_CHECKLIST_IDS_FIELD to emptyList<DocumentReference>()))

        FirebaseAuth.getInstance().currentUser?.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(displayName).build()
        )
    }

    fun addChecklist(
        title: String,
        items: List<ChecklistItem>,
        onSuccess: () -> Unit
    ) {
        val id = firestore.collection(CHECKLISTS_COLLECTION).document().id

        val checklist = Checklist(id, title, items)

        firestore.collection(CHECKLISTS_COLLECTION)
            .document(id)
            .set(checklist)
            .addOnSuccessListener { _ ->
                firestore.collection(USERS_COLLECTION)
                    .document(FirebaseUtil.getUserId())
                    .update(USER_CHECKLIST_IDS_FIELD, FieldValue.arrayUnion(id))
                    .addOnSuccessListener { onSuccess() }
            }
    }

    fun loadDisplayName(): String {
        return FirebaseUtil.getDisplayName()!!
    }

    fun loadChecklists(onSuccess: (Checklist) -> Unit) {
        firestore.collection(USERS_COLLECTION)
            .document(FirebaseUtil.getUserId())
            .get()
            .addOnSuccessListener { snapshot ->
                val checklistIds =
                    snapshot.data?.get(USER_CHECKLIST_IDS_FIELD) as? ArrayList<String>
                if (checklistIds != null) {
                    for (id in checklistIds) {
                        firestore.collection(CHECKLISTS_COLLECTION)
                            .document(id)
                            .get()
                            .addOnSuccessListener { onSuccess(it.toObject<Checklist>()!!) }
                    }
                }
            }
    }

    fun updateChecklistTitle(
        id: String,
        title: String,
        onSuccess: () -> Unit
    ) {
        firestore.collection(CHECKLISTS_COLLECTION)
            .document(id)
            .update(CHECKLIST_TITLE_FIELD, title)
            .addOnSuccessListener { onSuccess() }
    }

    fun updateChecklistItems(
        id: String,
        items: List<ChecklistItem>
    ) {
        firestore.collection(CHECKLISTS_COLLECTION)
            .document(id)
            .update(CHECKLIST_ITEMS_FIELD, items)
    }

    fun deleteChecklist(
        id: String,
        onSuccess: () -> Unit
    ) {
        firestore.collection(CHECKLISTS_COLLECTION)
            .document(id)
            .delete()
            .addOnSuccessListener {
                firestore.collection(USERS_COLLECTION)
                    .document(FirebaseUtil.getUserId())
                    .update(USER_CHECKLIST_IDS_FIELD, FieldValue.arrayRemove(id))
                    .addOnSuccessListener { onSuccess() }
            }
    }

    companion object {
        private const val CHECKLISTS_COLLECTION = "checklists"
        private const val CHECKLIST_TITLE_FIELD = "title"
        private const val CHECKLIST_ITEMS_FIELD = "items"

        private const val USERS_COLLECTION = "users"
        private const val USER_CHECKLIST_IDS_FIELD = "checklistIds"
    }
}
