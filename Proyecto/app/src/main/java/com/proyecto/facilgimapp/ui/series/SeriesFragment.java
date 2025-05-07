package com.proyecto.facilgimapp.ui.series;

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
import com.proyecto.facilgimapp.databinding.FragmentSeriesBinding;
import com.proyecto.facilgimapp.model.dto.SerieDTO;
import com.proyecto.facilgimapp.viewmodel.SeriesViewModel;
import java.util.List;

public class SeriesFragment extends Fragment implements SeriesAdapter.OnSeriesInteractionListener {
    private FragmentSeriesBinding binding;
    private SeriesViewModel viewModel;
    private SeriesAdapter adapter;
    private int exerciseId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            exerciseId = getArguments().getInt("exerciseId", -1);
        }

        viewModel = new ViewModelProvider(this).get(SeriesViewModel.class);
        adapter = new SeriesAdapter(this);

        binding.rvSeries.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSeries.setAdapter(adapter);

        binding.toolbarSeries.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.fabAddSeries.setOnClickListener(v -> showAddSeriesDialog());

        viewModel.getSeriesList().observe(getViewLifecycleOwner(), this::submitSeriesList);
        viewModel.loadSeries(exerciseId);
    }

    private void submitSeriesList(List<SerieDTO> list) {
        adapter.submitList(list);
    }

    private void showAddSeriesDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_series, null);
        EditText etReps = dialogView.findViewById(R.id.etReps);
        EditText etWeight = dialogView.findViewById(R.id.etWeight);

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_add_series)
                .setView(dialogView)
                .setPositiveButton(R.string.action_add, (d, which) -> {
                    int reps = Integer.parseInt(etReps.getText().toString());
                    double weight = Double.parseDouble(etWeight.getText().toString());
                    SerieDTO dto = new SerieDTO();
                    dto.setRepeticiones(reps);
                    dto.setPeso(weight);
                    viewModel.addSeries(dto, exerciseId);
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onDeleteSeries(SerieDTO serie) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setMessage(getString(R.string.confirm_delete_series))
                .setPositiveButton(R.string.action_delete, (d, which) ->
                        viewModel.deleteSeries(serie.getId(), exerciseId)
                )
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
