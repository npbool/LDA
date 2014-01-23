import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by npbool on 1/23/14.
 */
public class Dataset {
    public int nDoc;
    public int nVocab;
    public ArrayList<int[]> corpus;
    static Dataset loadFromFile(String filename){
        String line;
        int maxWordIndex = 0;
        ArrayList<int[]> corpus = new ArrayList<int[]>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while((line=reader.readLine())!=null){
                String[] wv = line.split(" ");
                int[] doc = new int[wv.length];
                for(int i=0;i<wv.length;++i){
                    doc[i] = Integer.parseInt(wv[i]);
                    maxWordIndex = Math.max(maxWordIndex, doc[i]);
                }
                corpus.add(doc);
            }
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        Dataset res = new Dataset();
        res.nDoc = corpus.size();
        res.nVocab = maxWordIndex+1;
        res.corpus = corpus;
        return res;
    }
}
