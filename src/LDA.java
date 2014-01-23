import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by npbool on 1/17/14.
 */
public class LDA {
    private int nTopic;
    private int nDoc;
    private int nVocab;
    private ArrayList<int[]> corpus;
    private double[] alpha;
    private double[] beta;

    private double[][] phi;

    private double defaultAlpha, defaultBeta;


    public LDA(int nTopic,double defaultAlpha, double defaultBeta){
        this.nTopic = nTopic;
        this.defaultAlpha = defaultAlpha;
        this.defaultBeta = defaultBeta;
    }

    public void train(Dataset data,int maxIter, int skip, int cycle){
        nDoc = data.nDoc;
        nVocab = data.nVocab;
        corpus = data.corpus;
        Random rand = new Random(42);

        alpha = new double[nTopic];
        Arrays.fill(alpha, defaultAlpha);
        beta = new double[nVocab];
        Arrays.fill(beta, defaultBeta);

        phi = new double[nTopic][nVocab];

        int[][] z = new int[nDoc][];
        for(int i=0;i<nDoc;++i){
            z[i] = new int[corpus.get(i).length];
            for(int j=0;j<z[i].length;++j){
                z[i][j] = rand.nextInt(nTopic);
            }
        }

        int[][] sumVocab = new int[nDoc][nTopic];
        int[][] sumDoc = new int[nVocab][nTopic];
        int[] sumOverVocab = new int[nTopic];
        int[] nWord = new int[nVocab];
        for(int i=0;i<nDoc;++i){
            int[] doc = corpus.get(i);
            for(int j=0;j<doc.length;++j){
                try{
                    int w = doc[j];
                    sumVocab[i][z[i][j]] += 1;
                    sumDoc[w][z[i][j]] += 1;
                    nWord[w]++;
                } catch(Exception e){
                    System.out.println(String.format("%d %d %d\n",i, j, z[i][j]));
                }
            }
        }
        for(int k = 0;k<nTopic;++k){
            for(int w = 0;w<nVocab;++w){
                sumOverVocab[k] += beta[w] + sumDoc[w][k];
            }
        }

        int[][] phi_sample = new int[nTopic][nVocab];


        //Gibbs sampling
        double[] prob = new double[nTopic];
        int nSample = 0;
        for(int iter = 0;iter<maxIter;++iter){
            if(iter % 100==0){
                System.out.println("Iter: "+iter);
            }
            for(int i=0;i<nDoc;++i){
                int[] doc = corpus.get(i);
                for(int j=0;j<doc.length;++j){
                    int w = doc[j];
                    int curZ = z[i][j];
                    double normalize = 0;
                    for(int k = 0;k<nTopic;++k){
                        prob[k] = alpha[k] + sumVocab[i][k] - Util.I(k==curZ);
                        prob[k] *= beta[w] + sumDoc[w][k] - Util.I(k==curZ);
                        prob[k] /= sumOverVocab[k] - Util.I(k==curZ);
                        normalize += prob[k];
                    }

                    double r = rand.nextDouble();
                    double accu = 0;
                    int newZ;
                    for(newZ = 0;newZ<nTopic;++newZ){
                        accu += prob[newZ]/normalize;
                        if(accu >= r){
                            break;
                        }
                    }
                    if(newZ ==nTopic){
                        System.out.println("ERRR");
                    }

                    z[i][j] = newZ;
                    sumVocab[i][curZ]--;
                    sumVocab[i][newZ]++;

                    sumDoc[w][curZ]--;
                    sumDoc[w][newZ]++;

                    sumOverVocab[curZ]--;
                    sumOverVocab[newZ]++;


                }
            }

            if(iter > skip && (iter-skip) % cycle==0){
                ++nSample;
                for(int i=0;i<nDoc;++i){
                    int[] doc = corpus.get(i);
                    for(int j=0;j<doc.length;++j){
                        int w = doc[j];
                        phi_sample[z[i][j]][w] ++;
                    }
                }
            }
        }
        for(int k=0;k<nTopic;++k){
            int sum = 0;
            for(int w=0;w<nVocab;++w){
                sum += phi_sample[k][w];
            }
            for(int w=0;w<nVocab;++w){
                phi[k][w] = phi_sample[k][w]*1.0/sum;
            }
        }
    }

    public void output(String filename){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

            for(int k=0;k<nTopic;++k){
                StringBuilder sb = new StringBuilder();

                for(int w=0;w<nVocab;++w){
                    sb.append(String.format("%.5f\t", phi[k][w]));
                }
                sb.append("\n");
                writer.write(sb.toString());
            }
            writer.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}

