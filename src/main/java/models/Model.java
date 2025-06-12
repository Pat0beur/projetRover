package models;

public class Model {
    // Exemple de donnée
    private String roverName;
    private int difficulte;
    private ModelCar modelCar;
    private Model model;

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
