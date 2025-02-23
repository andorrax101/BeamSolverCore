package solver.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Container defining all loadAssembly applied to a single beam model.
 */
public class LoadAssembly {
    private final List<LoadInstance> pointLoads = new ArrayList<>();
    private final List<LoadInstance> pointMoments = new ArrayList<>();
    private final List<LoadArray> distributedLoads = new ArrayList<>();
    private final List<LoadArray> distributedMoments = new ArrayList<>();

    public List<LoadInstance> getPointLoads(){
        return pointLoads;
    }
    public List<LoadInstance> getPointMoments(){
        return pointMoments;
    }
    public List<LoadArray> getDistributedLoads(){
        return distributedLoads;
    }
    public List<LoadArray> getDistributedMoments(){
        return distributedMoments;
    }

    public void addPointForce(LoadInstance instance){
        pointLoads.add(instance);
    }
    public void addPointMoment(LoadInstance instance){
        pointMoments.add(instance);
    }
    public void addDistributedForce(LoadInstance... instances){
        distributedLoads.add(LoadArray.of(instances));
    }
    public void addDistributedForce(List<LoadInstance> instances){
        distributedLoads.add(LoadArray.of(instances.toArray(new LoadInstance[0])));
    }
    public void addDistributedMoment(LoadInstance... instances){
        distributedMoments.add(LoadArray.of(instances));
    }
    public void addDistributedMoment(List<LoadInstance> instances){
        distributedMoments.add(LoadArray.of(instances.toArray(new LoadInstance[0])));
    }
}
