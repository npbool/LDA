/**
 * Created by npbool on 1/17/14.
 */
public class Main {
    public static void main(String[] argv){
        int nTopic = 10;
        LDA lda = new LDA(nTopic, 0.5, 0.5);
        Dataset data = Dataset.loadFromFile("/Users/npbool/Project/PRBM/data/"+"input.txt");
        lda.train(data, 200000, 100, 50);
        lda.output("output.txt");
    }
}
