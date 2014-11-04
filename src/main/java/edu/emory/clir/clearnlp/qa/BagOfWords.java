package edu.emory.clir.clearnlp.qa;

import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;
import edu.emory.clir.clearnlp.collection.map.IncMap1;
import edu.emory.clir.clearnlp.collection.map.ObjectIntHashMap;
import edu.emory.clir.clearnlp.collection.pair.ObjectIntPair;
import edu.emory.clir.clearnlp.tokenization.EnglishTokenizer;
import edu.emory.clir.clearnlp.util.StringUtils;
import org.json.*;

import java.io.*;
import java.util.*;

public class BagOfWords
{
    static StopwordsChecker stopwordsChecker = new StopwordsChecker();
    static EnglishTokenizer englishTokenizer = new EnglishTokenizer();
    static InputStream sourceQuestionsFile = null;
    static InputStream destinationQuestionsFile = null;

    static ObjectIntHashMap<String> wordsMap = new ObjectIntHashMap<String>();
    static int wordsMapCounter = 0;

    static List<SparseFeatureVector> sourceQuestionsVectors = new ArrayList<>();
    static List<SparseFeatureVector> destinationQuestionVectors = new ArrayList<>();

    public static void main(String [] args)
    {
        if (args.length != 2)
        {
            System.out.println("Run with two parameters: BagOfWords [source_questions_file], " +
                    "[destination_questions_file]");
            System.exit(1);
        }

        try
        {
            sourceQuestionsFile = new FileInputStream(args[0]);
            destinationQuestionsFile = new FileInputStream(args[1]);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

        createStructures(sourceQuestionsFile, destinationQuestionsFile);
        calculateCosineSimilarities(sourceQuestionsVectors, destinationQuestionVectors);
    }

    private static void createStructures(InputStream sourceFile, InputStream destinationFile)
    {
        sourceQuestionsVectors = createVectors(sourceFile);
        destinationQuestionVectors = createVectors(destinationFile);
    }

    private static List<SparseFeatureVector> createVectors(InputStream fileStream)
    {
        List<SparseFeatureVector> temp = new ArrayList<>();

        StringBuilder responseStrBuilder = new StringBuilder();
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(fileStream, "UTF-8"));
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONArray array = new JSONArray(responseStrBuilder.toString());

        for (int i = 0; i < array.length(); i++)
        {
            String questionContent = array.getJSONObject(i).getString("title");

            SparseFeatureVector a = getSparseVector(questionContent);
            temp.add(a);
        }

        return temp;
    }

    private static SparseFeatureVector getSparseVector(String string)
    {
        SparseFeatureVector sparseFeatureVector = new SparseFeatureVector(true);
        IncMap1<Integer> wordsHashMap = new IncMap1<>();
        List<String> wordsList = englishTokenizer.tokenize(string);

        for (String word: wordsList)
        {
            String wordSimplified = StringUtils.toLowerCase(StringUtils.toSimplifiedForm(word));
            if (! stopwordsChecker.isStopword(wordSimplified) &&
                    ! wordsMap.containsKey(wordSimplified))
                wordsMap.add(wordSimplified, wordsMapCounter++);

            int wordId = wordsMap.get(wordSimplified);
            wordsHashMap.add(wordId);
        }

        List<ObjectIntPair<Integer>> it = wordsHashMap.toList(0);
        for (int i = 0; i < it.size(); i++)
        {
            sparseFeatureVector.addFeature(it.get(i).o, 1);
        }

        return sparseFeatureVector;
    }

    private static void calculateCosineSimilarities(List<SparseFeatureVector> sourceQuestions,
                                                    List<SparseFeatureVector> destinationQuestions)
    {
        for (int i = 0; i < sourceQuestions.size(); i++)
        {
            for (int j = 0; j < destinationQuestions.size(); j++){
                System.out.println("Between src " + i + ", and dest " + j + ", cosine similarity = " +
                        calculateCosineSimilarity(sourceQuestions.get(i), destinationQuestions.get(j)));
            }
        }
    }

    private static double calculateCosineSimilarity(SparseFeatureVector vector1, SparseFeatureVector vector2)
    {
        double dotProduct = calculateDotProduct(vector1, vector2);
        double magnitudeOfVector1 = calculateMagnitudeOfVector(vector1);
        double magnitudeOfVector2 = calculateMagnitudeOfVector(vector2);

        return dotProduct / (magnitudeOfVector1 * magnitudeOfVector2);
    }

    private static double calculateDotProduct(SparseFeatureVector vector1, SparseFeatureVector vector2)
    {

        double dotProduct = 0;
        for (int i = 0 ; i < vector1.size(); i++)
        {
            int index_v1 = vector1.getIndex(i);
            int j = 0;
            while (j < vector2.size())
            {
                if (vector2.getIndex(j) == index_v1)
                {
                    dotProduct += (vector1.getWeight(i) * vector2.getWeight(j));
                    break;
                }
                j++;
            }
        }

        return dotProduct;
    }

    private static double calculateMagnitudeOfVector(SparseFeatureVector vector)
    {
        double dotProduct = 0;

        for (int i = 0; i < vector.size(); i++)
        {
            dotProduct += Math.pow(vector.getWeight(i), 2);
        }

        return Math.sqrt(dotProduct);
    }
}
