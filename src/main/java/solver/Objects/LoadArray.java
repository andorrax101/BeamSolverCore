package solver.Objects;

/**
 * Denotes a continuous region of distributed loads
 */
public class LoadArray {
    private final LoadInstance[] loadInstances;

    public LoadInstance[] getLoadInstances(){
        return loadInstances;
    }

    public LoadArray(LoadInstance... inputLoadInstances){
        loadInstances = inputLoadInstances;
    }

    public static LoadArray of(LoadInstance... inputLoadInstances){
        return new LoadArray(inputLoadInstances);
    }
}
