package edu.emory.clir.clearnlp.qa;

import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;
import edu.emory.clir.clearnlp.collection.map.IntObjectHashMap;
import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BagOfWords {
    public static void main(String [] args){
        InputStream is = null;
        List<String> s1t = null;
        List<String> s2t = null;

        String s1 = "I have one cat and this cat has name Milo";
        String s2 = "My mother would love to have a cat, but her landlord does not allow that";

        EnglishTokenizer et = new EnglishTokenizer();

        try {
            is = new FileInputStream("test.txt");
            s1t = et.tokenize(s1);
            s2t = et.tokenize(s2);
            System.out.println(s1t.toString());
            System.out.println(s2t.toString());
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectIntHashMap<String> stringsMap = new ObjectIntHashMap<String>();
        ArrayList<ObjectIntPair<String>> list = new ArrayList<ObjectIntPair<String>>();
        list.add(0, new ObjectIntPair<String>("aaa", 1));
        int incrementer = 0;
        for (String token: s1t){
            if (! stringsMap.containsKey(token)){
                stringsMap.add(token, incrementer);
                incrementer++;
            }
        }

        for (String token: s2t){
            if (! stringsMap.containsKey(token)){
                stringsMap.add(token, incrementer);     
                incrementer++;
            }
        }

        /* read and add all features */

        //System.out.println(stringsMap.toString());
        //stringsMap.putAll(s1t);
        //stringsMap.putAll(s2t);

        SparseFeatureVector sfv1 = new SparseFeatureVector(true);
        SparseFeatureVector sfv2 = new SparseFeatureVector(true);

        sfv1.addFeature(0, 2);
        sfv1.addFeature(1, 3);
        sfv1.addFeature(10, 5);

        System.out.println(sfv1);
        System.out.println(Arrays.toString(sfv1.getIndexes()));

    }
}
