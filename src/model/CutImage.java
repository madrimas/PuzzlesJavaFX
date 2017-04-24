package model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.ImagePattern;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madrimas on 23.04.2017.
 */
public class CutImage {

    static public List<Tile> getTileList(File file){
        List<Tile> tempTileList = new ArrayList<>(9);
        int tileCounter = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                BufferedImage image = getImageFromFile(file);
                BufferedImage part = image.getSubimage(j*100, i*100, 100, 100);
                Tile tempTile = new Tile(100,100,part,tileCounter++);
                tempTile.setFill(new ImagePattern(SwingFXUtils.toFXImage(tempTile.getPartOfImage(), null)));
                tempTile.setLayoutX(20+j*110);
                tempTile.setLayoutY(20+i*110);
                tempTileList.add(tempTile);
            }
        }
        return tempTileList;
    }
    static private BufferedImage getImageFromFile(File file){
        try{
            BufferedImage image = ImageIO.read(file);
            if( (image.getHeight()!=300) && (image.getWidth()!=300) ){
                Image tempImage = image.getScaledInstance(300,300, Image.SCALE_SMOOTH);
                BufferedImage tempBufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics2D = tempBufferedImage.createGraphics();
                graphics2D.drawImage(tempImage, 0, 0, null);
                graphics2D.dispose();
                return tempBufferedImage;
            }
            return image;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
