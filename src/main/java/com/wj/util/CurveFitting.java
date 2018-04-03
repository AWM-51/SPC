package com.wj.util;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.List;

public class CurveFitting {
    public double[] CurveFittingParameters(List<Double> x,List<Double> y){
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for(int i=0;i<x.size();++i){
            obs.add(x.get(i),y.get(i));
        }
        double[] parameters = GaussianCurveFitter.create().fit(obs.toList());
        return parameters;
    }

}
