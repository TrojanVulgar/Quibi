package com.easyplex.data.model.credits;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class MovieCreditsResponse {

    @SerializedName("id")
    private Integer id;
    @SerializedName("cast")
    private List<Cast> casts;
    @SerializedName("crew")
    private List<Cast> crews;

    public MovieCreditsResponse(Integer id, List<Cast> casts, List<Cast> crews) {
        this.id = id;
        this.casts = casts;
        this.crews = crews;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Cast> getCasts() {
        return casts;
    }

    public void setCasts(List<Cast> casts) {
        this.casts = casts;
    }

    public List<Cast> getCrews() {
        return crews;
    }

    public void setCrews(List<Cast> crews) {
        this.crews = crews;
    }

}
