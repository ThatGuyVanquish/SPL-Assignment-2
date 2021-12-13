package bgu.spl.mics;


import bgu.spl.mics.application.objects.Model;

public class FinishedTestedEvent  implements Event<Model> {
    private Model model;
    public FinishedTestedEvent(Model m){
        this.model =m;
    }

    public Model getModel() {
        return model;
    }
}
