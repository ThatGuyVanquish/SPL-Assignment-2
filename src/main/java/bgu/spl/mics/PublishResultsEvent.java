package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.Vector;

public class PublishResultsEvent implements Event<Model>{ // Not sure Event<Future<Model>> is the correct implementation
     private Model _studentModel;
     public PublishResultsEvent(Model studentModel){this._studentModel = studentModel;}

     public Model get_studentModel() {
          return _studentModel;
     }
}
