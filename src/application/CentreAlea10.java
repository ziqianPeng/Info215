package application;
//Q1 En choississant une méthode d'initialisation des centres avec aléa,
// Faites tourner l'algo de gaussienne avec 10 gaussienne et avec 10 conditions initiales différentes.
// Calculer pour chacune le score. Comparer les résultats obtenus entre la meilleur et la pire partition.

import gaussien.MixGauss;
import partie1.LoadSavePNG;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class CentreAlea10 {

    /*sauvegarder le resultat de classification par MixGauss sous forme d'une image png et d'une image png inversée */
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

        BufferedImage bui_out = new BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage bui_out0 = new BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
        for(int i=0 ; i<height ; i++) {
            for(int j=0 ; j<width ; j++) {
                bui_out.setRGB(j, i, tabColor[i * width + j].getRGB());
                bui_out0.setRGB(j, i, 255-tabColor[i * width + j].getRGB());
            }
        }

        ImageIO.write(bui_out, "PNG", new File(path+"Inverse" +nomPng +".png"));
        ImageIO.write(bui_out0, "PNG", new File(path+ nomPng +".png"));
    }


    public static void main(String[] args) throws IOException {
        System.out.println("CentreAlea10 avec gaussien.MixGauss");

        /** charger les donnees**/
        String path = "/home/sylviepeng/IdeaProjects/ReseauDeNeurones/ProjetNonSupervise/";
        String imageMMS = path + "mms.png";
        BufferedImage bui = ImageIO.read(new File(imageMMS));
        path = path + "ctrAleaImg/";

        int width = bui.getWidth();
        int height = bui.getHeight();
        double[][] rgbData = LoadSavePNG.loadImgData(bui); // width*hight points en 3D


        /**initialiser les centres, les densites et les echelles**/
        int D = 3; // 3 dimensions = R G B
        int k = 10; // 10 centres
        int nbrCI = 10;
        double[] score = new double[nbrCI];

        //initialiser les 10  centres avec 10 conditions initiales différentes.
        for(int ci = 0; ci <nbrCI; ci++) {
            System.out.println("conditions initiales numero " +ci);
            double[][] centres = MixGauss.initCentre(rgbData, k);

            double[] densites = new double[k];
            double[][] echellesCarre = new double[k][D];
            for (int i = 0; i < k; i++) { //initialiser tous les densite à 1./k
                densites[i] = 1. / k;
                for (int j = 0; j < D; j++) {//initialiser tous les echelle à 1
                    echellesCarre[i][j] = 0.5;
                }
            }

            /**la mixture de Gaussienne**/
            double[][] ass = MixGauss.gaussien(rgbData,centres,densites,echellesCarre);

            /**sauvegarder l'image**/
            saveImg(rgbData, centres, ass, bui, path, "AleaCtr"+ci);

            /**calculer le score**/
            score[ci] = MixGauss.score(rgbData, centres, densites, echellesCarre);
        }

        for (int scr = 0; scr < nbrCI; scr ++){
            System.out.println(score[scr]);
        }

    }


}
