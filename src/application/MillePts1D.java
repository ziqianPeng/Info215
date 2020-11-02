package application;
//Générer 1000 points en une dimension, avec 500 qui seront distribués
// selon une gaussienne centrée en -2 et de variance 0.2, et une autre centrée en 3 et de variance 1.5.

import gaussien.MixGauss;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class MillePts1D {

    /*créer le fichier contient les données pour l'histogramme*/
    public static void figureFile(double[][] X, int nbcase) throws IOException {
        double gmax = 3+1.5, gmin = -2-0.2;
        double[][] ghisto = TasGaussien.histogramme(gmin,gmax,nbcase,X);

        FileWriter fw = new FileWriter("histoMillePts1D.d");
        for(int i =0; i< nbcase ; i++) {
            fw.write((ghisto[0][i]-2.2)+","+ ghisto[1][i] + "\n");
        }
        fw.close();
    }

    public static void main(String[] args) throws IOException {
        int k = 2; // deux centres
        int D = 1; // point en 1D
        int size = 1000;
        double gauss1 = -2, var1 = 0.2, gauss2 = 3, var2 = 1.5;
        double[][] X = new double[size][D]; // 1000 points en D dimensions

        /** position des donnees **/
        Random r = new Random();
        int nbr1 = 0, nbr2 = 0;
        while (nbr1+nbr2 != size) {
            double d = r.nextGaussian();
            if (d > gauss1 - var1 && d < gauss1 + var1 && nbr1 < 500){ // -2.2 < d < -1.8
                X[nbr1+nbr2][0] = d;
                nbr1++;
            }
            if (d > gauss2 - var2 && d < gauss2 + var2 && nbr2 < 500){ // 1.5 < d < 4.5
                X[nbr1+nbr2][0] = d;
                nbr2++;
            }
        }

        /**initialiser les centres, les densites et les echelles**/
        double[][] centres = MixGauss.initCentre(X, k);
        System.out.println(Arrays.toString(centres[0]));
        System.out.println(Arrays.toString(centres[1]));

        double[] densites = new double[k];
        double[][] echellesCarre = new double[k][D];
        for (int i = 0; i < k; i++) { //initialiser tous les densite à 1./k
            densites[i] = 1. / k;
            for (int j = 0; j < D; j++) {//initialiser tous les echelle à 1
                echellesCarre[i][j] = 1;
            }
        }


        /**la mixture de Gaussienne**/
        double[][] ass = new double[X.length][centres.length];
        ass = MixGauss.gaussien(X, centres, densites, echellesCarre);

        /**verification**/
        for (int i = 0; i < centres.length; i++) {
            System.out.println("Pos centre " + i + ": " + centres[i][0]);
            System.out.println("La densité " + i + " = " + densites[i]); //==500
            System.out.println("L'échelle " + i + " = " + Math.sqrt(echellesCarre[i][0]) + "\n");
        }
        double score = MixGauss.score(X,centres,densites,echellesCarre);
        System.out.println("score == " + score);

        /**histogramme**/
        int nbcase = 16;
 /*     double gmax = 3+1.5, gmin = -2-0.2;
        double[][] ghisto = TasGaussien.histogramme(gmin,gmax,nbcase,X);

        FileWriter fw = new FileWriter("histoMillePts1D.d");
        for(int i =0; i< nbcase ; i++) {
            fw.write((ghisto[0][i]-2)+","+ ghisto[1][i] + "\n");
        }
        fw.close();
*/
       figureFile(X,nbcase);
    }
}
