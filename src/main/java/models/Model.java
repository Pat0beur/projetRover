package models;

public class Model {
    // Exemple de donnée
    private String roverName;
    private int difficulte;

    public Model() {
        this.roverName = "Curiosity";
        this.difficulte = 1;
    }
    public Model(int a) {
        this.roverName = "Curiosity";
        this.difficulte = a;
    }
    public String getRoverName() {
        return roverName;
    }
    // public Model setModel(){
    //     return model;
    // }
    public int getDifficulte(){
        return this.difficulte;
    }
    public void setDifficulte(int a){
        this.difficulte = a;
    }
    public void setRoverName(String name) {
        this.roverName = name;
    }
}
