package gaussien;

import partie1.LoadSavePNG;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class MixGauss {

    /*calculer la distance entre le point d1 et le point  centre*/
    private static double dist(double[] d1, double[] centre) {
        double dist2 = 0;
        for(int i = 0; i < d1.length; i++) {
            dist2 += Math.pow((d1[i]-centre[i]), 2);
        }
        return Math.sqrt(dist2);
    }

    /*trouver et return la diatance minimale entre la donnée X et les idx premières centres*/
    private static double distMin(double[] X, double[][] centres, int idx) {
        double dmin = dist(X,centres[0]);
        for(int c = 1; c < idx; c++) {
            double d = dist(X, centres[c]);
            if(d < dmin) { dmin = d;}
        }
        return dmin;
    }

    /*Initialiser aléatoirement les centres pour une mixture gaussienne
    telle que tous les centres sont assez loin entre elles
    * X           double[][], les données à traiter
    * nbrCentres  int, le nombre de centre(i.e centre)
    * return un tableau 2D de centres bien initialisées
    */
    public static double[][] initCentre(double[][] X , int nbrCentres) {
        double[][] centres = new double[nbrCentres][X[0].length];
        //initialise aléatoirement le 1er centre
        Random r1 = new Random();
        int idx0 = r1.nextInt(X.length-1);
        centres[0] = Arrays.copyOf(X[idx0], X[idx0].length);

        //pour les autres centres
        for(int c = 1; c < centres.length; c++) {
            double[] dist_min = new double[X.length];
            for(int i = 1; i < X.length; i++) {
                //la distance entre la donnee X[i] et la centre la plus proche
                dist_min[i] = distMin(X[i], centres, c);
            }
            double dmax = dist_min[0];
            int idx = 0;//l'indix du prochain centre
            for(int j = 1; j< dist_min.length; j++) {
                if(dmax < dist_min[j]) {
                    dmax = dist_min[j];
                    idx = j;
                }
            }
            centres[c] = Arrays.copyOf(X[idx], X[idx].length);
        }
        return centres;
    }

    /**II pour assignement**/
    /*Calculer le numérateur de la formule de probabilité */
    private static double probaUnite(double[] X, double[] centre, double densite, double[] echelleCarre) {
        double res = densite;
        for(int i = 0; i< X.length; i++) {
            res *= (1./Math.sqrt(2*Math.PI*echelleCarre[i]))*Math.exp(Math.pow(X[i]-centre[i],2)/(-2*echelleCarre[i]));
        }
        //System.out.println((res));
        return res;
    }

    /*Calculer et return la probabitité qu'une donnée X appartient au cluster k
    // k     int indice de centre (i.e de cluster)
    * */
    private static double probaGauss(double[] X, double[][] centres, double[] densites, double[][] echellesCarre, int k) {
        double proba = probaUnite(X, centres[k], densites[k], echellesCarre[k]);
        //System.out.println("hello" + densites[k]);
        double somme = 0;

        for(int ctr = 0; ctr < centres.length; ctr++ ) {
            somme += probaUnite(X, centres[ctr], densites[ctr], echellesCarre[ctr]);
            }
        return proba/somme;
    }

    /*Assigner
     * X             une liste de points à traiter
     * centres       chaque centre (parmi les k centres) par une position dans l'espace à D dimensions
     * densites      un tableau 1D qui stocke la densité de chaque cluster  (rho)
     * echelleCarre  un tableau 2D qui stocke l'échelle de chaque cluster mais au carré  (sigma)
     * return la tableau d'assignement qui stocke les probabilités d'appartenances de données dans X
     */
    public static double[][] Assigner(double[][] X, double[][] centres, double[] densites, double[][] echellesCarre) {
        double[][] ass = new double[X.length][centres.length];
        //pour chaque pts dans X
        for(int i = 0; i< X.length; i++) {
            for(int c =0; c< centres.length; c++) {
                ass[i][c] = probaGauss(X[i], centres, densites, echellesCarre, c);
            }
        }
/*        //test
        for(int i = 0; i< X.length; i++) {
            double s = 0;
            for(int c =0; c< centres.length; c++) {
                s += ass[i][c];
            }
            //somme de probabilite varie entre 0.99999999, 1 et 1.0000000000000002
            if(Math.abs(s-1)>0.000000000001){
                System.out.println("erreur somme proba != 1. indice =" + i + " s = " + s);
            }
        }
 */
        return ass;
    }

    /**III Deplct et maj**/

    /*cqlculer et renvoyer les RK de tous les clusters selon la formule donnée*/
    private static double[] Rk(double[][] ass) {
        double[] Rk = new double[ass[0].length];
        for (int c = 0; c < ass[0].length; c++) {
            Rk[c] = 0;
            for (int i = 0; i < ass.length; i++) {
                Rk[c] += ass[i][c];
            }
        }
        return Rk;
    }

    /*mettre à jour tous les centres selon la formule, calculer
     et renvoyer la distance de déplacement de ces centres lorsque la mit à jour*/
    private static double majCentre(double[][] X, double[][] centres, double[][] ass, double[] RK) {
        double distDep = 0;
        for(int c =0; c < centres.length; c++) {//pour chaque cluster(centre)

            double[] ancienCtr = Arrays.copyOf(centres[c], centres[c].length);//on garde son ancienne valeur
            for(int j =0; j < centres[0].length; j++) {//pour chaque coord du centre
                double coordCentre_cj = 0;
                for(int i =0; i < X.length; i++){//on rajoute tous les rc*Xi,j
                    coordCentre_cj += X[i][j]*ass[i][c];
                }
                centres[c][j] = coordCentre_cj/RK[c];//on met a jour les centres
            }
            distDep += dist(ancienCtr,centres[c]);//on cumule la distance du deplacement

        }
        return distDep;
    }

    /*mettre à jour les échelles au carré de tous les cluster selon la formule correspondante */
    private static void majEcheCarre(double[][] X, double[][] centres, double[][] ass, double[][] echelelsCarre, double[] RK) {
        for(int c =0; c < centres.length; c++) {//pour chaque cluster(centre)
            for(int j =0; j < centres[0].length; j++) {//pour chaque coord du centre

                double coordEchelle_cj = 0;
                for(int i =0; i < X.length; i++){//on rajoute tous les (ri,c)*(Xi,j-Mc,j)
                    coordEchelle_cj += Math.pow((X[i][j]-centres[c][j]),2)*ass[i][c];
                }
                echelelsCarre[c][j] = coordEchelle_cj/RK[c];//on met a jour les echelles

            }
        }
    }

    /*mettre à jour les densités de tous les cluster selon la formule correspondante*/
    private static void majDensite(double[][] X, double[] RK, double[] densites){
        double moy = 1./X[0].length;// pour diminuer le nbr des divisions
        for(int i = 0; i< RK.length; i++) {
            densites[i] = RK[i]*moy;
        }
    }

    /*déplacer les centres et mettre à jour les paramètres pour l'apprendissage et la classification*/
    public static double Deplct(double[][] X, double[][] centres, double[][] ass,double[] densite,double[][] echellesCarre) {
        double[] RK = Rk(ass);
        double distDep = majCentre(X,centres,ass,RK);
        //System.out.println("distDep = " + distDep);
        majEcheCarre(X,centres,ass, echellesCarre,RK);
        majDensite(X,RK,densite);
        return distDep;
    }

    /*trouver l'indix du chiffre maximum dans la liste double[] data*/
    public static int findMaxIdx(double[] data){
        double proba = data[0];
        int res = 0;
        for(int i = 1; i< data.length; i++){
            if(proba < data[i]){
                proba = data[i];
                res = i;
            }
        }
        return res;
    }

    /**IV la mixture de gaussienne**/
    public static double[][] gaussien(double[][] rgbData, double[][] centres,double[] densites, double[][] echellesCarre){
        /**la mixture de Gaussienne**/
        double eps = 0.001;
        double maj = 10;
        double[][] ass = new double[rgbData.length][centres.length];
        while (maj > eps) {
            ass = MixGauss.Assigner(rgbData, centres, densites, echellesCarre);
            maj = MixGauss.Deplct(rgbData, centres, ass, densites, echellesCarre);
        }

        return ass;
    }

    /**V le score**/
    /* score et scoreUnite pour calculer le score d'une partition*/
    public static double scoreUnite(double[] X, double[][] centres, double[] densite,double[][] echellesCarre){
        double scrUnite = 0;
        for(int c = 0; c < centres.length; c++){
            scrUnite += probaUnite(X,centres[c],densite[c],echellesCarre[c]);
        }
        return Math.log(scrUnite);
    }

    public static double score(double[][] X, double[][] centres, double[] densite,double[][] echellesCarre){
        double scr = 0;
        for(int i = 0; i < X.length; i++){
            scr += scoreUnite(X[i], centres, densite, echellesCarre);
        }
        return scr/X.length;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Je suis dans gaussien.MixGauss");

        System.out.println("test1");
        // creation d'un jeu de donnes simples pour tester l'algo
        int D=2; // deux dimensions
        int k=2; // deux centres

        double[][] X = new double[6][D]; // 6 points en D dimensions

        // position des donnees
        X[0][0] = -3;   X[0][1] = 1;
        X[1][0] = -2.5; X[1][1] = -0.5;
        X[2][0] = -4;   X[2][1] = 0;
        X[3][0] = 2;    X[3][1] = 2;
        X[4][0] = 2.5;  X[4][1] = -0.5;
        X[5][0] = 1.5;  X[5][1] = -1;

        //initialiser les centres, les densites et les echelles
        double[][] centres = initCentre(X,k);
        System.out.println(Arrays.toString(centres[0]));
        System.out.println(Arrays.toString(centres[1]));
        double[] densites = new double[k];
        double[][] echellesCarre = new double[k][D];
        for(int i = 0; i < k; i++){ //initialiser tous les densite à 1./k
            densites[i] = 1./k;
            for(int j=0; j< D; j++){//initialiser tous les echelle à 1
                echellesCarre[i][j] = 1;
            }
        }

        double eps=0.001;
        double maj = 10;
        double[][] ass = new double[X.length][centres.length];
        while(maj>eps) {
            ass = Assigner(X,centres,densites,echellesCarre);
            maj = Deplct(X,centres,ass,densites,echellesCarre);
        }

        // verification
        for(int i=0; i<X.length; i++) {
            System.out.println("Le proba pts "+i+ " appartient au cluster 0: "+ ass[i][0]);
        }
        for(int i=0; i<centres.length; i++) {
            System.out.println("Pos centre "+i+": "+centres[i][0]+" "+centres[i][1]);
        }

    }
}
