
package controller.controller;

import java.util.LinkedList;

import model.Descripteme;
import model.Property;

public class PropertyExtractController implements controller.controller.Observable{

	private Property mPropriete;
	private LinkedList<Observer> ObsMomentNames;
	
	public PropertyExtractController(Property moment) {
		this.mPropriete = moment;
		ObsMomentNames = new LinkedList<Observer>();
	}

	@Override
	public void update(Object value) {
		
		//System.out.println("UPDATE "+value);
		mPropriete.setDescriptemes(((LinkedList<Descripteme>) value));
		for(Observer obs : ObsMomentNames) {
			obs.updateVue(this, value);
		}
	}

	@Override
	public void addObserver(Observer o) {
		ObsMomentNames.add(o);
	}
	
	public void setPropriete(Property p) {
		this.mPropriete = p;
	}

	@Override
	public void updateModel(Object value) {
		this.mPropriete = (Property) value;
	}
	
	@Override
	public void RemoveObserver(Observer o) {
		ObsMomentNames.remove(o);
	}
	
	public Property getProperty() {
		return mPropriete;
	}
}
