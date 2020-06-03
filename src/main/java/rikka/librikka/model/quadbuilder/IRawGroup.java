package rikka.librikka.model.quadbuilder;

public interface IRawGroup<T extends IRawGroup> extends IRawModel<T>, IRawGroupWrapper<T, IRawElement> {
    default void add(IRawElement... rawModels) {
        for (IRawElement rawModel : rawModels) {
        	getElements().add(rawModel);
        }
    }

    default void merge(IRawGroup group) {
    	getElements().addAll(group.getElements());
    }
}
