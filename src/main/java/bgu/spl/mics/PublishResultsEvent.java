package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Model>{ // Not sure Event<Future<Model>> is the correct implementation
     private Model model;

     public PublishResultsEvent(Model model){ this.model = model; }

     public Model getModel() {
          return this.model;
     }
}
