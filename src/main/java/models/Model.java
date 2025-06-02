package models;

public class Model {
    // Exemple de donnée
    private String roverName;

    public Model() {
        this.roverName = "Curiosity";
    }

    public String getRoverName() {
        return roverName;
    }

    public void setRoverName(String name) {
        this.roverName = name;
    }
}
