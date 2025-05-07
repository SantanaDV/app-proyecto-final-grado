package com.proyecto.facilgimapp.ui.types;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.proyecto.facilgimapp.R;
import com.proyecto.facilgimapp.databinding.FragmentTypeListBinding;
import com.proyecto.facilgimapp.model.dto.TipoEntrenamientoDTO;
import com.proyecto.facilgimapp.viewmodel.TypeViewModel;

public class TypeListFragment extends Fragment implements TypeAdapter.OnTypeInteractionListener {
    private FragmentTypeListBinding binding;
    private TypeViewModel viewModel;
    private TypeAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTypeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(TypeViewModel.class);
        adapter = new TypeAdapter(this);

        binding.rvTypes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTypes.setAdapter(adapter);

        binding.toolbarTypes.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.fabAddType.setOnClickListener(v -> showAddOrEditDialog(null));

        viewModel.getTypes().observe(getViewLifecycleOwner(), types -> adapter.submitList(types));
        viewModel.loadTypes();
    }

    private void showAddOrEditDialog(@Nullable TipoEntrenamientoDTO editType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_type, null);
        EditText etName = dialogView.findViewById(R.id.etTypeName);
        if (editType != null) etName.setText(editType.getNombre());

        builder.setTitle(editType == null ? "Nuevo tipo" : "Editar tipo")
                .setView(dialogView)
                .setPositiveButton(editType == null ? "Crear" : "Guardar", (d, w) -> {
                    String name = etName.getText().toString();
                    if (editType == null) viewModel.addType(name);
                    else viewModel.updateType(editType.getId(), name);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onEdit(TipoEntrenamientoDTO type) {
        showAddOrEditDialog(type);
    }

    @Override
    public void onDelete(TipoEntrenamientoDTO type) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar tipo")
                .setMessage("¿Eliminar “" + type.getNombre() + "”?")
                .setPositiveButton("Eliminar", (d, w) -> viewModel.deleteType(type.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
