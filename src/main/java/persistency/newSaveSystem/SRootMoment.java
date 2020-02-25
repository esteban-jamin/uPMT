package persistency.newSaveSystem;

import components.modelisationSpace.moment.model.Moment;
import components.modelisationSpace.moment.model.RootMoment;
import persistency.newSaveSystem.serialization.ObjectSerializer;
import persistency.newSaveSystem.serialization.Serializable;
import utils.autoSuggestion.AutoSuggestions;

import java.util.ArrayList;

public class SRootMoment extends Serializable<RootMoment> {

    //General info
    public static final int version = 1;
    public static final String modelName = "rootMoment";

    public ArrayList<SMoment> submoments;

    public SRootMoment(ObjectSerializer serializer) {
        super(serializer);
    }

    public SRootMoment(RootMoment modelReference) {
        super(modelName, version, modelReference);

        this.submoments = new ArrayList<>();
        for(Moment m: modelReference.momentsProperty())
            submoments.add(new SMoment(m));
    }


    @Override
    protected void addStrategies() {

    }

    @Override
    protected void read() {
        submoments = serializer.getArray(serializer.setListSuffix(SMoment.modelName), SMoment::new);
    }

    @Override
    protected void write(ObjectSerializer serializer) {
        serializer.writeArray(serializer.setListSuffix(SMoment.modelName), submoments);
    }

    @Override
    protected RootMoment createModel() {
        RootMoment rm = new RootMoment();
        AutoSuggestions.getAutoSuggestions().setRootMoment(rm);

        for(SMoment sm: submoments)
            rm.addMoment(sm.convertToModel());

        return rm;
    }
}
