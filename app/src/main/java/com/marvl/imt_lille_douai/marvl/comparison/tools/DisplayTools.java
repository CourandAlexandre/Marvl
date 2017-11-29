package com.marvl.imt_lille_douai.marvl.comparison.tools;

import com.marvl.imt_lille_douai.marvl.comparison.image.ComparedImage;

import static org.bytedeco.javacpp.opencv_core.NORM_L2;
import static org.bytedeco.javacpp.opencv_features2d.drawMatches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_shape;
import org.bytedeco.javacpp.opencv_core.DMatchVector;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_features2d.BFMatcher;
import org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;

public class DisplayTools {

    public static void displayComparisonBetweenTwoImage(String pathToImg1, String pathToImg2) {
        Mat[] images = new Mat[2];
        KeyPointVector keyPoints1 = new KeyPointVector();
        KeyPointVector keyPoints2 = new KeyPointVector();

        SIFT sift;

        BFMatcher matcher = new BFMatcher(NORM_L2,false);
        DMatchVector matches = new DMatchVector(); // DMatchVectorVector matchesKnn = new DMatchVectorVector();

        images[0] = GlobalTools.loadImg(pathToImg1);
        images[1] = GlobalTools.loadImg(pathToImg2);

        Loader.load(opencv_calib3d.class);
        Loader.load(opencv_shape.class);

        sift = SIFT.create(SiftTools.nFeatures,SiftTools.nOctaveLayers,SiftTools.contrastThreshold,SiftTools.edgeThresold,SiftTools.sigma);

        sift.detect(images[0], keyPoints1);
        sift.detect(images[1], keyPoints2);

        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        sift.compute(images[0], keyPoints1, descriptors1);
        sift.compute(images[1], keyPoints2, descriptors2);

        // Match descriptors | matcher.knnMatch(descriptors1,descriptors2,matchesKnn,5);
        matcher.match(descriptors1, descriptors2, matches);

        DMatchVector bestMatches = MatchingTools.selectBestMatcher(matches, 25);

        // Draw Matches
        Mat imageMatches = new Mat();
        drawMatches(images[0], keyPoints1, images[1], keyPoints2, bestMatches, imageMatches);

        GlobalTools.displayImg("Comparison", imageMatches);

        matcher.close();
    }

    /*public static void displayBestImageInDataBankComparedTo(String imgPath, String pathToDataBank) {
        System.out.println("Best image : ");

        // Calculate and display closest image compared to Coca_1
        String bestComparedImg = SimilitudeTools.getMostSimilitudeImageComparedToDataBank(imgPath, pathToDataBank);
        displayComparisonBetweenTwoImage(imgPath, bestComparedImg);
    }*/

    /*public static void displayXNbOfBestImageInDataBankComparedTo(String imgPath,Integer nbOfImage, String pathToDataBank) {
        System.out.println("Top "+nbOfImage+" image :");

        // Calculate and display 5 closest image compared to Coca_1
        ArrayList<ComparedImage> best5ComparedImg = SimilitudeTools.getOrderedNbOfMostSimilitudeComparedToDataBank(imgPath, pathToDataBank, nbOfImage);

        for(int i=0; i < best5ComparedImg.size(); i++) {
            System.out.println("N°"+(i+1)+" :"+best5ComparedImg.get(i).getImageName() + " | Distance = "+best5ComparedImg.get(i).getImageDistance());
            displayComparisonBetweenTwoImage(imgPath, best5ComparedImg.get(i).getImageName());
        }

        displayBestClassInArrayOfBestComparedImage(best5ComparedImg);
    }*/

    /*public static void displayBestClassInArrayOfBestComparedImage(ArrayList<ComparedImage> comparedImageArray) {
        // Find best class
        HashMap<String, Integer> bestClassMap = SimilitudeTools.getMostSimilitudeClassContainedInMostSimilitudeArray(comparedImageArray);
        Iterator<String> it = bestClassMap.keySet().iterator();
        String bestClass = (String) it.next();
        Integer bestCpt = (Integer) bestClassMap.get(bestClass);

        System.out.println("The best class out of 5 top image is : "+bestClass+" with "+bestCpt+" hit");
    }*/
}
