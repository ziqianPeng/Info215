package partie1;

import gaussien.MixGauss;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TriParCouleur {

    /*sauvegarder le resultat de classification par MixGauss sous forme d'une image png et d'une image png inversée
    * path  la répertoire où on stockera l'image
    * */
    public static void saveImg(double[][] rgbData, double[][] centres, double[][] ass, BufferedImage bui, String path) throws IOException {
        int width = bui.getWidth();
        int height = bui.getHeight();
        Color[] centersColor = new Color[centres.length];
        Color[] tabColor = new Color[rgbData.length];

        for (int i = 0; i < centres.length ; i++) {
            centersColor[i] = new Color((float) centres[i][0], (float) centres[i][1], (float) centres[i][2]);
            System.out.println("Pos centreColor "+i+": "+centersColor[i].toString());
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

        ImageIO.write(bui_out, "PNG", new File(path+"InverseParti1.png"));
        ImageIO.write(bui_out0, "PNG", new File(path+"Parti1.png"));

    }

    public static void main(String[] args) throws IOException {
        System.out.println("TriParCouleur avec gaussien.MixGauss");

        /** charger les donnees**/
        String path = "/home/sylviepeng/IdeaProjects/ReseauDeNeurones/ProjetNonSupervise/";
        String imageMMS = path + "mms.png";
        BufferedImage bui = ImageIO.read(new File(imageMMS));

        double[][] rgbData = LoadSavePNG.loadImgData(bui); // width*hight points en 3D


        /**initialiser les centres, les densites et les echelles**/
        int D = 3; // 3 dimensions = R G B
        int k = 8; // 8 centres 4 couleurs
        //double[][] centres = MixGauss.initCentre(rgbData,k);

        //initialiser les centres pas aleatoirement pour l'image inverse
        double[][] centres = new double[k][D];
        centres[0][0] = 1;   centres[0][1] = 1;  centres[0][2] = 1; //blanc inverse de noir
        centres[1][0] = 0;   centres[1][1] = 1; centres[1][2] = 1; //cyan inverse de rouge
        centres[2][0] = 1;   centres[2][1] = 0; centres[2][2] = 1; //magenta inverse de vert
        centres[3][0] = 1;   centres[3][1] = 1; centres[3][2] = 0; //jaune inverse de bleu
        centres[4][0] = 0;  centres[4][1] = 0; centres[4][2] = 1;  //bleu inverse de jaune
        centres[5][0] = 0;  centres[5][1] = 1;centres[5][2] = 0; //vert inverse pour un autre rouge dans mms
        centres[6] = rgbData[0]; //la couleur du fond
        centres[7][0] = 0;  centres[7][1] = 0;centres[7][2] = 0; //noir inverse du blanc pour le reflet

        double[] densites = new double[k];
        double[][] echellesCarre = new double[k][D];
        for(int i = 0; i < k; i++){ //initialiser tous les densite à 1./k
            densites[i] = 1./k;
            for(int j=0; j< D; j++){//initialiser tous les echelle à 1
                echellesCarre[i][j] = 0.5;
            }
        }

        /**la mixture de Gaussienne**/
        double[][] ass = MixGauss.gaussien(rgbData,centres,densites,echellesCarre);

        // verification
        for(int i=0; i<centres.length; i++) {
            System.out.println("Pos centre "+i+": "+centres[i][0]+" "+centres[i][1]+ " "+centres[i][2]);
        }

        /**sauvegarder l'image**/
        saveImg(rgbData, centres, ass, bui, path);

        /**calculer le score**/
        System.out.println("score = " + MixGauss.score(rgbData,centres,densites,echellesCarre));
    }
}