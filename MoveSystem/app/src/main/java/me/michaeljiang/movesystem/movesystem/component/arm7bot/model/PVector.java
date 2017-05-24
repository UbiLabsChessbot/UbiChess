package me.michaeljiang.movesystem.movesystem.component.arm7bot.model;

/**
 * Created by Michael on 2016/9/9 0009.
 */
public class PVector {
    public double x,y,z;

    public PVector(){
        this.x = this.y = this.z = 0.0;
    }

    public PVector(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public void add(PVector p){
        x += p.x;
        y += p.y;
        z += p.z;
    }

    public void normalize(){
        double l = Math.sqrt(x * x + y * y + z * z);
        x /= l;
        y /= l;
        z /= l;
    }

    public double dot(PVector p){
        return x * p.x + y * p.y + z * p.z;
    }

    public double dist(PVector p){
        double dist_x = x - p.x;
        double dist_y = y - p.y;
        double dist_z = z - p.z;
        return Math.sqrt(dist_x * dist_x + dist_y * dist_y + dist_z * dist_z);
    }

}
