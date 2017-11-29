package com.marvl.imt_lille_douai.marvl.comparison.tools;


import com.marvl.imt_lille_douai.marvl.comparison.image.ComparedImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SimilitudeTools {

    public static String getMostSimilitudeImageComparedToDataBank(String pathToImg, String pathToDataBank) {
        HashMap<String, Float> imgValueMap = generateMapSimilitudeOfImageComparedToDataBank(pathToImg, pathToDataBank);
        Set<String> imgSet = imgValueMap.keySet();
        Iterator<String> i = imgSet.iterator();

        String bestImg = (String) i.next();
        Float bestDistance = (Float) imgValueMap.get(bestImg);

        while(i.hasNext()) {
            String tmpImg = (String) i.next();
            Float tmpDistance = (Float) imgValueMap.get(tmpImg);

            // seeking for the smallest distance
            if(bestDistance > tmpDistance) {
                bestImg = tmpImg;
                bestDistance = tmpDistance;
            }

        }

        return bestImg;
    }

    public static ArrayList<ComparedImage> getOrderedNbOfMostSimilitudeComparedToDataBank(String pathToImg, String pathToDataBank, Integer nbOfImgToReturn){
        HashMap<String, Float> imgValueMap = generateMapSimilitudeOfImageComparedToDataBank(pathToImg, pathToDataBank);

        if(nbOfImgToReturn > imgValueMap.size()) {
            return null;
        }else {
            ArrayList<ComparedImage> similitudeArray = GlobalTools.convertHashMapToArrayListOfComparedImage(imgValueMap,pathToDataBank);
            ArrayList<ComparedImage> sortedArray = new ArrayList<>();

            for(int i=0; i<nbOfImgToReturn; i++) {
                Integer bestIdx = getIndexOfMostSimilitudeInComparedImageList(similitudeArray);
                sortedArray.add(i,similitudeArray.get(bestIdx));
                similitudeArray.remove(bestIdx+0); // +0 because if not it doesnt delete object
            }

            return sortedArray;
        }

    }

    public static Integer getIndexOfMostSimilitudeInComparedImageList(ArrayList<ComparedImage> comparedImageArray) {
        Float bestDist = comparedImageArray.get(0).getImageDistance();
        Integer bestIdx = 0;

        for(int i=1; i<comparedImageArray.size();i++) {
            Float dist = comparedImageArray.get(i).getImageDistance();

            // seeking for the smallest distance
            if(bestDist > dist) {
                bestDist = dist;
                bestIdx = i;
            }
        }

        return bestIdx;
    }

    public static HashMap<String, Float> generateMapSimilitudeOfImageComparedToDataBank(String pathToImg, String pathToDataBank) {
        HashMap<String, Float> imgValueMap = new HashMap<>();

        File dataBank = new File(pathToDataBank);
        File[] dataBankFiles = dataBank.listFiles();

        for(File file : dataBankFiles) {
            String dataBankFileToCompare = pathToDataBank + file.getName();

            imgValueMap.put(dataBankFileToCompare, MatchingTools.getBestMatchesAvgBetweenTwoImage(pathToImg, dataBankFileToCompare));
        }

        return imgValueMap;
    }

    public static HashMap<String, Integer> getMostSimilitudeClassContainedInMostSimilitudeArray(ArrayList<ComparedImage> comparedImageArray) {
        HashMap<String, Integer> classValueMap = new HashMap<>();
        HashMap<String, Integer> bestClassValueMap = new HashMap<>();

        for(int i=0; i<comparedImageArray.size(); i++) {
            if(classValueMap.containsKey(comparedImageArray.get(i).getImageClass())){
                classValueMap.put(comparedImageArray.get(i).getImageClass(), classValueMap.get(comparedImageArray.get(i).getImageClass()) + 1);
            }else {
                classValueMap.put(comparedImageArray.get(i).getImageClass(), 1);
            }
        }

        Set<String> classSet = classValueMap.keySet();
        Iterator<String> it = classSet.iterator();

        String className = (String) it.next();
        Integer classCpt = (Integer) classValueMap.get(className);

        String bestClass = className;
        Integer bestCpt = classCpt;

        while(it.hasNext()) {
            String tmpName = (String) it.next();
            Integer tmpCpt = (Integer) classValueMap.get(tmpName);

            if(bestCpt < tmpCpt) {
                bestClass = tmpName;
                bestCpt = tmpCpt;
            }
        }

        bestClassValueMap.put(bestClass, bestCpt);

        return bestClassValueMap;
    }
}