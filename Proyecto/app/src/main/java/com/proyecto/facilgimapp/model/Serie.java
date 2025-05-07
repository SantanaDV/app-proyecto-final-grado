package com.proyecto.facilgimapp.model;

import com.google.gson.annotations.SerializedName;

public class Serie {
    private int id;

    @SerializedName("exerciseId")
    private int exerciseId;

    private int reps;
    private double weight;

    public int getId() {
        return id;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
