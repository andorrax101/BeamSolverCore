package solver.Objects;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
/**
 * Container defining all loadAssembly applied to a single beam model.
 */
public class LoadAssembly {
    private final List<LoadInstance> pointLoads = new ArrayList<>();
    private final List<LoadInstance> pointMoments = new ArrayList<>();
    private final List<LoadPair> distributedLoads = new ArrayList<>();
    private final List<LoadPair> distributedMoments = new ArrayList<>();

    public void addPointForce(LoadInstance instance){
        pointLoads.add(instance);
    }
    public void addPointMoment(LoadInstance instance){
        pointMoments.add(instance);
    }
    public void addDistributedForce(LoadInstance startInstance, LoadInstance endInstance){
        distributedLoads.add(LoadPair.of(startInstance, endInstance));
    }
    public void addDistributedMoment(LoadInstance startInstance, LoadInstance endInstance){
        distributedMoments.add(LoadPair.of(startInstance, endInstance));
    }
}
