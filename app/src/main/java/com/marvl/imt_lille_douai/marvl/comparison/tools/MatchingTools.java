package com.marvl.imt_lille_douai.marvl.comparison.tools;

import static org.bytedeco.javacpp.opencv_features2d.drawMatches;
import static org.bytedeco.javacpp.opencv_core.NORM_L2;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_calib3d;
import org.bytedeco.javacpp.opencv_shape;
import org.bytedeco.javacpp.opencv_core.DMatch;
import org.bytedeco.javacpp.opencv_core.DMatchVector;
import org.bytedeco.javacpp.opencv_core.DMatchVectorVector;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_features2d.BFMatcher;
import org.bytedeco.javacpp.opencv_xfeatures2d.SIFT;

import java.util.Arrays;


public class MatchingTools {

    public static Float getBestMatchesAvgBetweenTwoImage(String pathToImg1, String pathToImg2) {
        Mat[] images = new Mat[2];
        KeyPointVector keyPoints1 = new KeyPointVector();
        KeyPointVector keyPoints2 = new KeyPointVector();

        SIFT sift;

        BFMatcher matcher = new BFMatcher(NORM_L2,false);
        DMatchVector matches = new DMatchVector();
        DMatchVectorVector matchesKnn = new DMatchVectorVector();

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

        return MatchingTools.getBestMatchesAvg(bestMatches, 25);
    }

    public static DMatchVector selectBestMatcher(DMatchVector matches, int numberToSelect) {
        DMatch[] sorted = DMatchVectorToArray(matches);

        Arrays.sort(sorted, (a, b) -> {
            return a.lessThan(b) ? -1 : 1;
        });

        DMatch[] bestArray = Arrays.copyOf(sorted, numberToSelect);

        return new DMatchVector(bestArray);
    }

    public static DMatch[] DMatchVectorToArray(DMatchVector matches) {
        assert matches.size() <= Integer.MAX_VALUE;
        int matchesSize = (int) matches.size();

        // Convert keyPoints to Scala sequence
        DMatch[] result = new DMatch[matchesSize];

        for(int i=0; i < matchesSize ; i++) {
            result[i] = new DMatch(matches.get(i));
        }

        return result;
    }

    public static Float getBestMatchesAvg(DMatchVector bestMatches, Integer nbOfBestMatch) {
        float avg = 0;

        for(int i=0; i<bestMatches.size(); i++) {
            avg += bestMatches.get(i).distance();
        }

        return avg/nbOfBestMatch;
    }
}