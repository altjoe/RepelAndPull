import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class RepelAndPull extends PApplet {

ArrayList<SpiralVerlet> vers = new ArrayList<SpiralVerlet>();
Recording record;
public void setup() {
    
    background(255);

    for (int i = 1; i <= 100; i++){
        SpiralVerlet ver = new SpiralVerlet(width/2, height/2, radians(i), 2*PI*i/10);
        ver.display();
        vers.add(ver);
    }
    record = new Recording();
    record.start();
}

public void draw() {
    // background(255);
    translate(width/2, width/2);
    for (SpiralVerlet ver : vers){
        ver.move();
        ver.display();
    }
    record.control();
}

class Force {
    float radius;
    PVector loc;

    public Force(float x, float y, float r){
        loc = new PVector(x, y);
        radius = r;
    }
}

class Stack {
    ArrayList<PVector> pts = new ArrayList<PVector>();
    int s;
    public Stack(int s){
        this.s = s;
    }
    public void push(PVector pt){
        pts.add(0, pt);
        if (pts.size() > s){
            pts.remove(pts.size()-1);
        }
    }
}
int from = color(204, 102, 0);
int to = color(0, 102, 153);

class SpiralVerlet {
    PVector curr;
    PVector prev;
    PVector loc;
    PVector prevCenter = new PVector(0,0);
    float speed = 0.01f;
    float angle;
    float rotation;
    Stack stack;
    int c;
    public SpiralVerlet(float x, float y, float a, float r){
        loc = new PVector(x, y);
        curr = new PVector(0, 0);
        prev = new PVector(0-speed, 0);
        angle = a;
        rotation = r;
        stack = new Stack(4);
        // float perc = float(int(random(0,5)))/5.0;
        // c = lerpColor(from, to, perc);
    }

    public void move(){
        PVector diff = PVector.sub(curr, prev);
        diff.rotate(angle);
        diff.mult(1.0f + speed);
        prev = curr.copy();
        curr = PVector.add(diff, curr);

        float dist = PVector.sub(prevCenter, curr).mag();
        if (dist > togo && !triggered){
            trigger();
        } else if (triggered && currSpeed() < 0.01f){
            trigger();
            triggered = false;
        }
    }

    public float currSpeed(){
        return PVector.sub(curr, prev).mag();
    }

    public void display(){
        pushMatrix();
        rotate(rotation);
        // ellipse(curr.x, curr.y, 5, 5);
        // strokeWeight(int(random(1,3)));
        // noFill();
        fill(255);
        beginShape();
        for (PVector pt : stack.pts){
            curveVertex(pt.x, pt.y);
        }
        endShape();
        popMatrix();
        stack.push(curr);
    }

    float togo = width/3;
    boolean triggered = false;
    public void trigger(){
        angle *= -1.5f;
        speed *= -1;
        triggered = true;
        prevCenter = curr.copy();
        togo *= 9.0f/10.0f;
    }
}

class Recording {
    boolean recording = false;
    boolean stopped = false;
    int start_frame;
    int stop_frame;
    int frame_rate = 30;
    int recording_time = 200;

    public Recording() {
        
    }

    public void start(){
        if (recording == false && stopped == false) {
                recording = true;
                start_frame = frameCount;
                stop_frame = start_frame + (frame_rate * recording_time);
        }
    }

    public void control(){
        if (recording) {
            saveFrame("output/img-####.png");
            if (stop_frame < frameCount) {
                stopped = true;
                recording = false;
            }
            print(stop_frame, frameCount, '\n');
            if (stopped) {
                println("Finished.");
                System.exit(0);
            }
        }
    }
}
  public void settings() {  size(1080, 1080); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "RepelAndPull" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
