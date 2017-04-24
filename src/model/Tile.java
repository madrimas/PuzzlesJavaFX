package model;

import java.awt.image.BufferedImage;

/**
 * Created by madrimas on 23.04.2017.
 */
public class Tile extends javafx.scene.shape.Rectangle {
    private BufferedImage partOfImage;
    private int numberOfImage;

    public int getNumberOfImage(){
        return numberOfImage;
    }
    public void setNumberOfImage(int numberOfImage){
        this.numberOfImage = numberOfImage;
    }
    public BufferedImage getPartOfImage() {
        return partOfImage;
    }
    public void setPartOfImage(BufferedImage partOfImage) {
        this.partOfImage = partOfImage;
    }

    public Tile(double width, double height, BufferedImage partOfImage, int numberOfImage){
        super(width, height);
        this.partOfImage = partOfImage;
        this.numberOfImage = numberOfImage;
    }
    public Tile(BufferedImage partOfImage, int numberOfImage){
        this.partOfImage = partOfImage;
        this.numberOfImage = numberOfImage;
    }
}
