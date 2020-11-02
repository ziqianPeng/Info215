package application;
//Faites varier le nombre de centres de K=2 jusqu'à K=10. Pour chaque valeur de K,
// faites tourner l'algorihme avec 10 conditions initiales différentes.

import gaussien.MixGauss;
import partie1.LoadSavePNG;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CentreMeuilleurNbr {

    /*sauvegarder le resultat de classification par MixGauss sous forme d'une image png et d'une image png inversée
     * path  la répertoire où on stockera l'image
     * nomPng le nom de l'image
     * */
    public static void saveImg(double[][] rgbData, double[][] centres, double[][] ass, BufferedImage bui, String path, String nomPng) throws IOException {
        int width = bui.getWidth();
        int height = bui.getHeight();
        Color[] centersColor = new Color[centres.length];
        Color[] tabColor = new Color[rgbData.length];

        for (int i = 0; i < centres.length ; i++) {
            centersColor[i] = new Color((float) centres[i][0], (float) centres[i][1], (float) centres[i][2]);
            //System.out.println("Pos centreColor "+i+": "+centersColor[i].toString());
        }
        for (int i = 0; i < rgbData.length ; i++) {
            tabColor[i] = centersColor[MixGauss.findMaxIdx(ass[i])];
        }

        BufferedImage bui_out0 = new BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        for(int i=0 ; i<height ; i++) {
            for(int j=0 ; j<width ; j++) {
                bui_out0.setRGB(j, i, 255-tabColor[i * width + j].getRGB());
            }
        }

        ImageIO.write(bui_out0, "PNG", new File(path+ nomPng +".png"));
    }

    /*créer la fichier qui stocke la "meilleur" score pour k = 2 jusqu'à k = 10 */
    public static void figureScore(double[] scoreK){
        FileWriter fw;
        try {
            fw = new FileWriter("scoreK.d");
            for(int k = 2; k <scoreK.length+2; k++) {
                fw.write(k+" "+scoreK[k-2] + " ");
                fw.write("\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("CentreAlea10 avec gaussien.MixGauss");

        /* charger les donnees*/
        String path = "/home/sylviepeng/IdeaProjects/ReseauDeNeurones/ProjetNonSupervise/";
        String imageMMS = path + "mms.png";
        BufferedImage bui = ImageIO.read(new File(imageMMS));

        double[][] rgbData = LoadSavePNG.loadImgData(bui); // width*hight points en 3D
        path = path + "MeilleurKImg/";

        /*initialiser les centres, les densites et les echelles*/
        int D = 3; // 3 dimensions = R G B
        double scoresK[] = new double[9]; //les meilleurs scores pour k = 2 jusqu'q k = 10 (9 conditions)

        /**Pour k= 2 jusqu'à k = 10**/
        for (int k = 2; k <= 10; k++) {
            System.out.println("nombre de centre k = " + k);
            int nbrCI = 10;
            double[] score = new double[nbrCI];

            //initialiser les densites et les echelles
            double[] densites = new double[k];
            double[][] echellesCarre = new double[k][D];
            for (int i = 0; i < k; i++) { //initialiser tous les densite à 1./k
                densites[i] = 1. / k;
                for (int j = 0; j < D; j++) {//initialiser tous les echelle à 1
                    echellesCarre[i][j] = 0.5;
                }
            }
            double[][][] ass = new double[nbrCI][rgbData.length][k];
            double[][][] centres = new double[nbrCI][k][D];

            /**initialiser k centres avec 10 conditions initiales différentes**/
            for (int ci = 0; ci < nbrCI; ci++) {
                centres[ci] = MixGauss.initCentre(rgbData, k);

                /*la mixture de Gaussienne*/
                ass[ci] = MixGauss.gaussien(rgbData, centres[ci], densites, echellesCarre);

                /**calculer le score**/
                score[ci] = MixGauss.score(rgbData, centres[k-2], densites, echellesCarre);

                saveImg(rgbData, centres[ci], ass[ci], bui, path, "nbrCTR" + k +"CI"+ci);
            }

            int meilleurCI = MixGauss.findMaxIdx(score);

            for (int sc = 0; sc < score.length; sc++) {
                System.out.println("le score de " + k + " centres, CI "+ sc + " = " + score[sc]);
            }

            scoresK[k-2] = score[meilleurCI];

            /*sauvegarder l'image*/
            saveImg(rgbData, centres[meilleurCI], ass[meilleurCI], bui, path, "nbrCtr" + k );

        }

        for (int scr = 0; scr < scoresK.length; scr++) {
            System.out.println(scoresK[scr]);
        }

        figureScore(scoresK);
    }

}
