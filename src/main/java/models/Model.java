package models;

public class Model {
    // Exemple de donnée
    private String roverName;
    private int difficulte;
    private ModelCar modelCar;
    private Model model;
    private int Score;

    public Model() {
        this.roverName = "Curiosity";
        this.difficulte = 1;
        this.modelCar = new ModelCar();
    }
    public Model(int a) {
        this.roverName = "Curiosity";
        this.difficulte = a;
        this.modelCar = new ModelCar();
    }

    public void setScore(int a){
        this.Score = a;
    }
    public int getScore(){
        return Score;
    }

    public String getRoverName() {
        return roverName;
    }
    public int getDifficulte(){
        return this.difficulte;
    }
    public void setDifficulte(int a){
        this.difficulte = a;
    }
    public void setRoverName(String name) {
        this.roverName = name;
    }
    public void setModel(Model model){
        this.model = model;
    }
    public void setModelCar(ModelCar modelCar){
        this.modelCar = modelCar;
    }
}
