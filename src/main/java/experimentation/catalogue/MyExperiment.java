package experimentation.catalogue;

import experimentation.ExperimentDefinition;

public class MyExperiment implements ExperimentDefinition {
    @Override
    public String name() {
        return "bg_my_experiment";
    }
}
