package com.amr.coursemate.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amr.coursemate.data.db.AppDatabase
import com.amr.coursemate.data.repository.AppRepository
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
        ClassPageViewModel.Factory(AppRepository(AppDatabase.getInstance(requireContext())), classId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentClassPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = TranslationAdapter { translation ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete translation?")
                .setMessage("\"${translation.bangla}\" will be removed.")
                .setPositiveButton("Delete") { _, _ -> viewModel.deleteTranslation(translation) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        binding.recyclerTranslations.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.translations.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.fabAddTranslation.setOnClickListener { showAddTranslationDialog() }
        binding.btnNotes.setOnClickListener {
            showTextEditorDialog("Notes", viewModel.courseClass.value?.notes ?: "") { text ->
                viewModel.saveNotes(text)
            }
        }
        binding.btnHomework.setOnClickListener {
            showTextEditorDialog("Homework", viewModel.courseClass.value?.homework ?: "") { text ->
                viewModel.saveHomework(text)
            }
        }
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

    private fun showTextEditorDialog(title: String, initialText: String, onSave: (String) -> Unit) {
        val dialogBinding = DialogTextEditorBinding.inflate(layoutInflater)
        dialogBinding.etText.setText(initialText)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ -> onSave(dialogBinding.etText.text?.toString().orEmpty()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}