package com.amr.coursemate.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.model.Translation
import com.amr.coursemate.data.repository.AppRepository
import com.amr.coursemate.databinding.DialogAddNoteBinding
import com.amr.coursemate.databinding.DialogAddTranslationBinding
import com.amr.coursemate.databinding.DialogTextEditorBinding
import com.amr.coursemate.databinding.FragmentClassPageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ClassPageFragment : Fragment() {

    companion object {
        private const val ARG_CLASS_ID = "class_id"

        fun newInstance(classId: Long) = ClassPageFragment().apply {
            arguments = Bundle().apply { putLong(ARG_CLASS_ID, classId) }
        }
    }

    private var _binding: FragmentClassPageBinding? = null
    private val binding get() = _binding!!

    private val classId by lazy { requireArguments().getLong(ARG_CLASS_ID) }

    private val viewModel: ClassPageViewModel by viewModels {
        ClassPageViewModel.ClassPageViewModelFactory(
            AppRepository(AppDatabase.getInstance(requireContext())), classId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClassPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = TranslationAdapter(
            onLongClick = { translation ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete translation?")
                    .setMessage("\"${translation.bangla}\" will be removed.")
                    .setPositiveButton("Delete") { _, _ -> viewModel.deleteTranslation(translation) }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onNoteClick = { translation, existingNote ->
                if (existingNote != null) {
                    startActivity(NotesActivity.newIntent(requireContext(), classId, existingNote.id))
                } else {
                    showAddNoteForTranslationDialog(translation)
                }
            }
        )

        binding.recyclerTranslations.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.translationsWithNotes.observe(viewLifecycleOwner) { adapter.submitList(it) }

        viewModel.courseClass.observe(viewLifecycleOwner) { courseClass ->
            binding.tvDescription.text = courseClass?.description?.ifEmpty { "No description" }
        }

        viewModel.newlyCreatedNoteId.observe(viewLifecycleOwner) { noteId ->
            if (noteId != null) {
                startActivity(NotesActivity.newIntent(requireContext(), classId, noteId))
                viewModel.clearNewNoteId()
            }
        }

        binding.fabAddTranslation.setOnClickListener { showAddTranslationDialog() }
        binding.btnEditDescription.setOnClickListener { showDescriptionDialog() }
    }

    private fun showAddNoteForTranslationDialog(translation: Translation) {
        val dialogBinding = DialogAddNoteBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Note for \"${translation.bangla}\"")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val content = dialogBinding.etNoteContent.text?.toString()?.trim().orEmpty()
                if (content.isNotEmpty()) viewModel.addNoteForTranslation(translation.id, content)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDescriptionDialog() {
        val dialogBinding = DialogTextEditorBinding.inflate(layoutInflater)
        dialogBinding.etText.setText(viewModel.courseClass.value?.description ?: "")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Description")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                viewModel.saveDescription(dialogBinding.etText.text?.toString().orEmpty())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddTranslationDialog() {
        val dialogBinding = DialogAddTranslationBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Translation")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val bangla = dialogBinding.etBangla.text?.toString()?.trim().orEmpty()
                val arabic = dialogBinding.etArabic.text?.toString()?.trim().orEmpty()
                if (bangla.isNotEmpty() || arabic.isNotEmpty()) {
                    viewModel.addTranslation(bangla, arabic)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
