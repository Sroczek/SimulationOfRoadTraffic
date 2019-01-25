package app;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import simulation.Car;
import simulation.Road;
import geometry.Point2D;


public class CarDrawer {

    private ImageView carView;
    private Rotate rotation;
    private Car car;
    private Group root;


    public CarDrawer(Car car, Group root){
        this.car = car;
        this.root = root;

        this.carView = new ImageView(new Image(car.getImagePath()));
        this.rotation = new Rotate();
        carView.getTransforms().add(this.rotation);

        root.getChildren().add(carView);
    }

    public void draw(Road road){
        double scale = this.car.getPosition()/road.getLength();
        Point2D position = road.getBeginPoint().add(new Point2D(Point2D.vectorFromTO(road.getBeginPoint(),road.getEndPoint()).getX()*scale,Point2D.vectorFromTO(road.getBeginPoint(),road.getEndPoint()).getY()*scale));

        this.carView.setX(position.getX() - 0.5 * this.carView.getImage().getWidth());
        this.carView.setY(position.getY() - 0.5 * this.carView.getImage().getHeight());

        this.rotation.setPivotX(position.getX());
        this.rotation.setPivotY(position.getY());

        this.rotation.setAngle(road.getAngle());
    }

    public void delete(){
        this.root.getChildren().remove(this.carView);
    }

}

