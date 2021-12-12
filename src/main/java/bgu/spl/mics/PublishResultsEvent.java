package bgu.spl.mics;

import bgu.spl.mics.application.objects.Model;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.Vector;

public class PublishResultsEvent implements Event<Future<Model>>{ // Not sure Event<Future<Model>> is the correct implementation
     private Vector<Model> _studentModel;
     public PublishResultsEvent(Vector<Model> studentModel){this._studentModel = studentModel;}


}
