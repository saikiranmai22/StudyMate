package com.studymate.studymate.document.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimilarityCalculator {

    public double calculateCosineSimilarity(
            List<Double> vectorA,
            List<Double> vectorB
    ){
        if(vectorA==null || vectorB==null){
             throw new IllegalStateException("Vectors cannot be null");
        }
        if(vectorA.isEmpty() || vectorB.isEmpty()){
             throw new IllegalStateException("Vectors cannot be empty");
        }
        if(vectorA.size()!=vectorB.size()){
            throw new IllegalStateException("Vectors mst be the same dimensions");
        }
        double dotPtoduct=0.0;
        double magnitudeA=0.0;
        double magnitudeB=0.0;

        for(int i=0;i<vectorA.size();i++){
            double a = vectorA.get(i);
            double b= vectorB.get(i);

            dotPtoduct+=a*b;
            magnitudeA+=a*a;
            magnitudeB+=b*b;
        }

        if(magnitudeA==0.0 || magnitudeB==0.0){
            throw new IllegalStateException("Cannot calculate similarity for zero vectors");
        }
        return dotPtoduct/(Math.sqrt(magnitudeA)*Math.sqrt(magnitudeB));
    }
}
