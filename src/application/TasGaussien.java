package application;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TasGaussien {

    /*Créerle tableau qui va contenir
       0: les abcisses des cases de l'histogramme
       1: les valeurs pour chaque case
       */
    public static double[][] histogramme(double xmin, double xmax, int NbCases, double[][] data) {
        double[][] Histo = new double[2][NbCases];
        double largeurColonne = (xmax - xmin)/NbCases;
        //Calcule de la taille d'une case (largeur de chaque colonne dans l'histo)
        for(int j = 0; j< 2; j++) {
            for(int i= 0; i< NbCases; i++) {
                Histo[0][i] = i*largeurColonne;
                Histo[1][i] = 0;
            }
        }
        //Calcule des abcisses Histo[0][...]
        for(int i=0; i<data.length; i++) {
            //pour chaque valeur: trouver a quelle case elle appartient et incrementer de un l'histogramme
            if(data[i][0] != xmax) {
                int idx = (int)Math.floor((data[i][0]-xmin)/largeurColonne); // math.floor(double a) --return double
                Histo[1][idx] += 1;
            }else {
                Histo[1][NbCases-1]++;
            }
        }
        return Histo;
    }

    /*Calculer et renvoyer l'écart type */
    public static double ecartType(double[][] data, double moyen){
        double ecart = 0;
        for(int i = 0; i< data.length; i++) {
            ecart += Math.pow(data[i][0]-moyen, 2);
        }
        return Math.sqrt(ecart/data.length);
    }


    //test avec nextDouble
    public static void testRandom(int size, int nbcase) throws IOException {
        double[][] testdata = new double[size][0]; //en 1D
        double somme = 0;
        Random r = new Random();
        for(int i = 0; i< size; i++) {
            testdata[i][0] = r.nextDouble();
            somme += testdata[i][0];
        }
        //moyenne
        System.out.println("la moyen de mon échatillon est " + somme/size);
        double m = somme/100.;

        //ecart type: fang cha
        double ecart = ecartType(testdata,m);
        System.out.println("l'écart type = " + ecart);

        // histogramme
        double xmin = 1;  int mindx = 0;
        double xmax = 0;  int maxdx = 0;
        for(int i=0; i< size; i++) {
            if(xmin > testdata[i][0]) { xmin = testdata[i][0]; mindx = i; }
            if(xmax < testdata[i][0]) { xmax = testdata[i][0]; maxdx = i; }
        }
        System.out.println("mindx  = " + mindx + " maxdx = " + maxdx);
        double[][] histo = histogramme(xmin,xmax,nbcase,testdata);

        //visualiser histogramme
        FileWriter fw = new FileWriter("histoTestRandom.d");
        for(int i =0; i< nbcase ; i++) {
            fw.write(histo[0][i]+","+ histo[1][i] + "\n");
        }
        fw.close();
    }

    public static void main(String[] args) throws IOException {
        int size = 1000;
        int nbcase = 6;
        Random r = new Random();

        //test1 nextDouble
        testRandom(size,nbcase);

        //test2 gaussien
        double gxmin = -2.2;
        double gxmax = 4.8;
        double[][] gdata = new double[size][0];
        Random r1 = new Random();
        int nbr = 0;
        while(nbr != size) {
            double d = r.nextGaussian();
            if(d>gxmin && d < gxmax) {
                gdata[nbr][0] = d;
                nbr ++;
            }
        }
        double[][] ghisto = histogramme(gxmin,gxmax,nbcase,gdata);

        FileWriter fw1 = new FileWriter("histoTestGaussien.d");
        for(int i =0; i< nbcase ; i++) {
            fw1.write(ghisto[0][i]+","+ ghisto[1][i] + "\n");
        }
        fw1.close();

    }
}

